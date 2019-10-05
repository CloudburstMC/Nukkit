package cn.nukkit.level.provider.anvil;

import cn.nukkit.utils.Zlib;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.BitSet;
import java.util.zip.DataFormatException;

public class RegionFile implements Closeable {
    public static final byte COMPRESSION_GZIP = 1;
    public static final byte COMPRESSION_ZLIB = 2;
    private static final int SECTOR_HEADER_SIZE = 5;
    private static final int SECTOR_BYTES = 4096;
    private static final int SECTOR_INTS = 1024;
    private static final ByteBuffer EMPTY_SECTOR = ByteBuffer.allocateDirect(SECTOR_BYTES).asReadOnlyBuffer();
    private final int[] offsets = new int[SECTOR_INTS];
    private final int[] timestamps = new int[SECTOR_INTS];
    private final BitSet usedSectors = new BitSet();
    private final FileChannel channel;
    private int totalSectorsAvailable;

    public RegionFile(Path anvilRegionPath) throws IOException {
        this.channel = FileChannel.open(anvilRegionPath, StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE);
        if (channel.size() <= SECTOR_BYTES * 2) {
            if (channel.size() == 0) {
                channel.write(new ByteBuffer[]{EMPTY_SECTOR.duplicate(), EMPTY_SECTOR.duplicate()});
            } else {
                channel.write(ByteBuffer.allocate((int) (SECTOR_BYTES * 2 - channel.size())));
            }
        }

        if ((channel.size() & 0xfff) != 0) {
            // Needs to be a multiple of 4KB
            channel.write(ByteBuffer.allocate((int) (SECTOR_BYTES - (channel.size() & 0xfff))));
        }

        channel.position(0);
        totalSectorsAvailable = (int) (channel.size() / SECTOR_BYTES);
        usedSectors.set(0, 2);

        ByteBuffer offsets = ByteBuffer.allocate(SECTOR_BYTES * 2);
        while (offsets.hasRemaining()) {
            if (channel.read(offsets) == -1) {
                throw new EOFException();
            }
        }

        this.channel.read(offsets);
        offsets.flip();
        IntBuffer offsetInts = offsets.asIntBuffer();

        for (int i = 0; i < SECTOR_INTS; i++) {
            this.offsets[i] = offsetInts.get();
            int sectorNumber = this.offsets[i] >> 8;
            int occupiedSectors = this.offsets[i] & 0xff;
            if (this.offsets[i] != 0 && sectorNumber >= 0 && sectorNumber + occupiedSectors <= totalSectorsAvailable) {
                usedSectors.set(sectorNumber, sectorNumber + occupiedSectors + 1);
            }
        }

        for (int i = 0; i < SECTOR_INTS; i++) {
            timestamps[i] = offsetInts.get();
        }
    }

    private static boolean inBounds(int x, int z) {
        return x >= 0 && x < 32 && z >= 0 && z < 32;
    }

    public synchronized ByteBuf readChunk(int x, int z) throws IOException {
        Preconditions.checkArgument(inBounds(x, z), "blockPosition (%s, %s) is out of bounds (0 through 32)", x, z);
        Preconditions.checkArgument(hasChunk(x, z), "chunk (%s, %s) does not exist", x, z);
        int offset = getOffset(x, z);

        int sectorNumber = offset >> 8;
        int occupiedSectors = offset & 0xFF;

        if (sectorNumber + occupiedSectors > totalSectorsAvailable) {
            throw new IllegalArgumentException("Sector size is invalid for this chunk");
        }

        // Seek to the position in question.
        this.channel.position(sectorNumber * SECTOR_BYTES);

        // Read the entire sector.
        int sectorSize = occupiedSectors * SECTOR_BYTES;
        ByteBuf buffer = ByteBufAllocator.DEFAULT.ioBuffer(sectorSize);
        try {
            while (buffer.writerIndex() < sectorSize) {
                int written = this.channel.read(buffer.internalNioBuffer(buffer.writerIndex(), buffer.writableBytes()));
                if (written == -1) {
                    throw new EOFException();
                }
                buffer.writerIndex(buffer.writerIndex() + written);
            }
            // 4 bytes: big-endian int is the size of this sector
            // 1 byte: compression type - 1 is gzip, 2 is zlib
            int sectorLength = buffer.readInt();
            if (sectorLength > buffer.readableBytes()) {
                throw new IOException("Mismatched sector length (read " + occupiedSectors + " sectors, but length is " + sectorLength + " bytes)");
            }
            ByteBuf chunk = buffer.readSlice(sectorLength);
            byte type = chunk.readByte();

            ByteBuf uncompressed = ByteBufAllocator.DEFAULT.ioBuffer();
            try {
                switch (type) {
                    case COMPRESSION_GZIP:
                        Zlib.GZIP.inflate(chunk, uncompressed);
                        break;
                    case COMPRESSION_ZLIB:
                        Zlib.DEFAULT.inflate(chunk, uncompressed);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown compression type: " + type);
                }
                return uncompressed.retain();
            } catch (DataFormatException e) {
                throw new IOException("Unable to decompress chunk", e);
            } finally {
                uncompressed.release();
            }
        } finally {
            buffer.release();
        }
    }

    public synchronized void writeChunk(int x, int z, ByteBuf buffer) throws IOException {
        Preconditions.checkArgument(inBounds(x, z), "blockPosition (%s, %s) is out of bounds (0 through 32)", x, z);

        int offset = getOffset(x, z);
        int sectorNumber = offset >> 8;
        int sectorsAllocated = offset & 0xff;
        int sectorsNeeded = (buffer.readableBytes() + SECTOR_HEADER_SIZE) / SECTOR_BYTES + 1;

        if (sectorsNeeded >= 256) {
            throw new IllegalArgumentException("Writing this chunk would take too many sectors (limit is 255, but " + sectorsNeeded + " is needed)");
        }

        if (sectorNumber != 0 && sectorsAllocated == sectorsNeeded) {
            // We can overwrite the old sector(s).
            writeChunkInternal(sectorNumber, buffer);
        } else {
            // We must find new sectors to use. Clear currently allocated sectors.
            for (int i = 0; i < sectorsAllocated; ++i) {
                usedSectors.clear(sectorNumber + i);
            }

            // Now search for the required number of sectors.
            int runStart = 2;
            int runLength = 0;
            int currentSector = 2;
            while (runLength < sectorsNeeded) {
                if (usedSectors.length() >= currentSector) {
                    // We reached the end, and we will need to allocate a new sector.
                    break;
                }
                int nextSector = usedSectors.nextClearBit(currentSector + 1);
                if (currentSector + 1 == nextSector) {
                    runLength++;
                } else {
                    runStart = nextSector;
                    runLength = 1;
                }
                currentSector = nextSector;
            }

            // If we have enough sectors, then use it.
            if (runLength >= sectorsNeeded) {
                setOffset(x, z, runStart, sectorsNeeded);
                usedSectors.set(sectorNumber, sectorNumber + sectorsNeeded + 1);
                writeChunkInternal(runStart, buffer);
                setTimestamp(x, z, (int) (System.currentTimeMillis() / 1000));
            } else {
                // Need to allocate new sectors.
                int startSector = totalSectorsAvailable;
                channel.position(channel.size());
                usedSectors.set(startSector, startSector + sectorsNeeded + 1);

                ByteBuffer[] buffers = new ByteBuffer[sectorsNeeded];
                for (int i = 0; i < buffers.length; i++) {
                    buffers[i] = EMPTY_SECTOR.duplicate();
                }
                int bytesToWrite = SECTOR_BYTES * sectorsNeeded;
                while (bytesToWrite > 0) {
                    long written = channel.write(buffers);
                    bytesToWrite -= written;
                    if (written == 0) {
                        throw new IOException("Can't write any more filler");
                    }
                }

                totalSectorsAvailable += sectorsNeeded;
                setOffset(x, z, startSector, sectorsNeeded);
                writeChunkInternal(startSector, buffer);
                setTimestamp(x, z, (int) (System.currentTimeMillis() / 1000));
            }
        }
    }

    private void writeChunkInternal(int startSector, ByteBuf buffer) throws IOException {
        ByteBuffer header = ByteBuffer.allocateDirect(SECTOR_HEADER_SIZE);
        header.putInt(buffer.readableBytes() + 1).put((byte) 2);
        header.flip();

        channel.position(startSector * SECTOR_BYTES);

        long bytesToWrite = buffer.readableBytes() + header.remaining();
        ByteBuffer[] toWrite = new ByteBuffer[]{header, buffer.internalNioBuffer(buffer.readerIndex(), buffer.readableBytes())};
        while (bytesToWrite > 0) {
            long written = channel.write(toWrite);
            bytesToWrite -= written;
            if (written == 0) {
                throw new IOException("Can't write any more chunks");
            }
        }
    }

    private int getOffset(int x, int z) {
        return offsets[x + z * 32];
    }

    public boolean hasChunk(int x, int z) {
        return getOffset(x, z) != 0;
    }

    private void setOffset(int x, int z, int sectorStart, int sectorsOccupied) throws IOException {
        int num = x + z * 32;
        int encoded = sectorStart << 8 | sectorsOccupied;
        offsets[num] = encoded;
        ByteBuffer offsetNumber = ByteBuffer.allocate(Integer.BYTES).putInt(encoded);
        offsetNumber.flip();

        channel.position(num * 4);
        while (offsetNumber.hasRemaining()) {
            if (channel.write(offsetNumber) == 0) {
                throw new IOException("Can't write any more of the offset buffer!");
            }
        }
    }

    private void setTimestamp(int x, int z, int timestamp) throws IOException {
        int num = x + z * 32;
        timestamps[num] = timestamp;
        ByteBuffer tsNumber = ByteBuffer.allocate(Integer.BYTES).putInt(timestamp);
        tsNumber.flip();

        channel.position(SECTOR_BYTES + num * 4);
        while (tsNumber.hasRemaining()) {
            if (channel.write(tsNumber) == 0) {
                throw new IOException("Can't write any more of the timestamp buffer!");
            }
        }
    }

    @Override
    public void close() throws IOException {
        this.channel.force(true);
        this.channel.close();
    }
}
