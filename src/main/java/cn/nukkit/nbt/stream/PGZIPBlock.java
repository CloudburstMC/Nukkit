package cn.nukkit.nbt.stream;

import java.util.concurrent.Callable;

public class PGZIPBlock implements Callable<byte[]> {

    public static final int SIZE = 64 * 1024;

    /**
     * This ThreadLocal avoids the recycling of a lot of memory, causing lumpy performance.
     */
    protected final ThreadLocal<PGZIPState> STATE;

    // private final int index;
    protected final byte[] in = new byte[PGZIPBlock.SIZE];

    protected int in_length = 0;

    public PGZIPBlock(final PGZIPOutputStream parent) {
        this.STATE = new PGZIPThreadLocal(parent);
    }

    /*
     public Block(@Nonnegative int index) {
     this.index = index;
     }
     */
    // Only on worker thread
    @Override
    public byte[] call() throws Exception {
        // LOG.info("Processing " + this + " on " + Thread.currentThread());

        final PGZIPState state = this.STATE.get();
        // ByteArrayOutputStream buf = new ByteArrayOutputStream(in.length);   // Overestimate output size required.
        // DeflaterOutputStream def = newDeflaterOutputStream(buf);
        state.def.reset();
        state.buf.reset();
        state.str.write(this.in, 0, this.in_length);
        state.str.flush();

        // return Arrays.copyOf(in, in_length);
        return state.buf.toByteArray();
    }

    @Override
    public String toString() {
        return "Block" /* + index */ + "(" + this.in_length + "/" + this.in.length + " bytes)";
    }

}