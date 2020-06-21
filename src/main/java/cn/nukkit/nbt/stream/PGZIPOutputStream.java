package cn.nukkit.nbt.stream;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.*;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

/**
 * A multi-threaded version of {@link java.util.zip.GZIPOutputStream}.
 *
 * @author shevek
 */
public class PGZIPOutputStream extends FilterOutputStream {

    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    // private static final Logger LOG = LoggerFactory.getLogger(PGZIPOutputStream.class);
    private static final int GZIP_MAGIC = 0x8b1f;

    // TODO: Share, daemonize.
    private final ExecutorService executor;

    private final int nthreads;

    private final CRC32 crc = new CRC32();

    private final BlockingQueue<Future<byte[]>> emitQueue;

    // todo: remove after block guessing is implemented
    // array list that contains the block sizes
    private final IntList blockSizes = new IntArrayList();

    private int level = Deflater.BEST_SPEED;

    private int strategy = Deflater.DEFAULT_STRATEGY;

    private PGZIPBlock block = new PGZIPBlock(this/* 0 */);

    /**
     * Used as a sentinel for 'closed'.
     */
    private int bytesWritten = 0;

    // Master thread only
    public PGZIPOutputStream(final OutputStream out, final ExecutorService executor, final int nthreads) throws IOException {
        super(out);
        this.executor = executor;
        this.nthreads = nthreads;
        this.emitQueue = new ArrayBlockingQueue<Future<byte[]>>(nthreads);
        this.writeHeader();
    }

    /**
     * Creates a PGZIPOutputStream
     * using {@link PGZIPOutputStream#getSharedThreadPool()}.
     *
     * @param out the eventual output stream for the compressed data.
     * @throws IOException if it all goes wrong.
     */
    public PGZIPOutputStream(final OutputStream out, final int nthreads) throws IOException {
        this(out, PGZIPOutputStream.getSharedThreadPool(), nthreads);
    }

    /**
     * Creates a PGZIPOutputStream
     * using {@link PGZIPOutputStream#getSharedThreadPool()}
     * and {@link Runtime#availableProcessors()}.
     *
     * @param out the eventual output stream for the compressed data.
     * @throws IOException if it all goes wrong.
     */
    public PGZIPOutputStream(final OutputStream out) throws IOException {
        this(out, Runtime.getRuntime().availableProcessors());
    }

    public static ExecutorService getSharedThreadPool() {
        return PGZIPOutputStream.EXECUTOR;
    }

    protected static DeflaterOutputStream newDeflaterOutputStream(final OutputStream out, final Deflater deflater) {
        return new DeflaterOutputStream(out, deflater, 512, true);
    }

    public void setStrategy(final int strategy) {
        this.strategy = strategy;
    }

    public void setLevel(final int level) {
        this.level = level;
    }

    // Master thread only
    @Override
    public void write(final int b) throws IOException {
        final byte[] single = new byte[1];
        single[0] = (byte) (b & 0xFF);
        this.write(single);
    }

    // Master thread only
    @Override
    public void write(final byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }

    // Master thread only
    @Override
    public void write(final byte[] b, int off, int len) throws IOException {
        this.crc.update(b, off, len);
        this.bytesWritten += len;
        while (len > 0) {
            // assert block.in_length < block.in.length
            final int capacity = this.block.in.length - this.block.in_length;
            if (len >= capacity) {
                System.arraycopy(b, off, this.block.in, this.block.in_length, capacity);
                this.block.in_length += capacity;   // == block.in.length
                off += capacity;
                len -= capacity;
                this.submit();
            } else {
                System.arraycopy(b, off, this.block.in, this.block.in_length, len);
                this.block.in_length += len;
                // off += len;
                // len = 0;
                break;
            }
        }
    }

    // Master thread only
    @Override
    public void flush() throws IOException {
        // LOG.info("Flush: " + block);
        if (this.block.in_length > 0) {
            this.submit();
        }
        this.emitUntil(0);
        super.flush();
    }

    // Master thread only
    @Override
    public void close() throws IOException {
        // LOG.info("Closing: bytesWritten=" + bytesWritten);
        if (this.bytesWritten >= 0) {
            this.flush();

            PGZIPOutputStream.newDeflaterOutputStream(this.out, this.newDeflater()).finish();

            final ByteBuffer buf = ByteBuffer.allocate(8);
            buf.order(ByteOrder.LITTLE_ENDIAN);
            // LOG.info("CRC is " + crc.getValue());
            buf.putInt((int) this.crc.getValue());
            buf.putInt(this.bytesWritten);
            this.out.write(buf.array()); // allocate() guarantees a backing array.
            // LOG.info("trailer is " + Arrays.toString(buf.array()));

            this.out.flush();
            this.out.close();

            this.bytesWritten = Integer.MIN_VALUE;
            // } else {
            // LOG.warn("Already closed.");
        }
    }

    protected Deflater newDeflater() {
        final Deflater def = new Deflater(this.level, true);
        def.setStrategy(this.strategy);
        return def;
    }

    /*
     * @see http://www.gzip.org/zlib/rfc-gzip.html#file-format
     */
    private void writeHeader() throws IOException {
        this.out.write(new byte[]{
            (byte) PGZIPOutputStream.GZIP_MAGIC, // ID1: Magic number (little-endian short)
            (byte) (PGZIPOutputStream.GZIP_MAGIC >> 8), // ID2: Magic number (little-endian short)
            Deflater.DEFLATED, // CM: Compression method
            0, // FLG: Flags (byte)
            0, 0, 0, 0, // MTIME: Modification time (int)
            0, // XFL: Extra flags
            3 // OS: Operating system (3 = Linux)
        });
    }

    // Master thread only

    // Master thread only
    private void submit() throws IOException {
        this.emitUntil(this.nthreads - 1);
        this.emitQueue.add(this.executor.submit(this.block));
        this.block = new PGZIPBlock(this/* block.index + 1 */);
    }

    // Emit If Available - submit always
    // Emit At Least one - submit when executor is full
    // Emit All Remaining - flush(), close()
    // Master thread only
    private void tryEmit() throws IOException, InterruptedException, ExecutionException {
        for (; ; ) {
            final Future<byte[]> future = this.emitQueue.peek();
            // LOG.info("Peeked future " + future);
            if (future == null) {
                return;
            }
            if (!future.isDone()) {
                return;
            }
            // It's an ordered queue. This MUST be the same element as above.
            this.emitQueue.remove();
            final byte[] toWrite = future.get();
            this.blockSizes.add(toWrite.length);  // todo: remove after block guessing is implemented
            this.out.write(toWrite);
        }
    }

    /**
     * Emits any opportunistically available blocks. Furthermore, emits blocks until the number of executing tasks is less than taskCountAllowed.
     */
    private void emitUntil(final int taskCountAllowed) throws IOException {
        try {
            while (this.emitQueue.size() > taskCountAllowed) {
                // LOG.info("Waiting for taskCount=" + emitQueue.size() + " -> " + taskCountAllowed);
                final Future<byte[]> future = this.emitQueue.remove(); // Valid because emitQueue.size() > 0
                final byte[] toWrite = future.get();  // Blocks until this task is done.
                this.blockSizes.add(toWrite.length);  // todo: remove after block guessing is implemented
                this.out.write(toWrite);
            }
            // We may have achieved more opportunistically available blocks
            // while waiting for a block above. Let's emit them here.
            this.tryEmit();
        } catch (final ExecutionException e) {
            throw new IOException(e);
        } catch (final InterruptedException e) {
            throw new InterruptedIOException();
        }
    }

}