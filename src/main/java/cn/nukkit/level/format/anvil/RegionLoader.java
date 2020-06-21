package cn.nukkit.level.format.anvil;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.generic.BaseRegionLoader;
import cn.nukkit.utils.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class RegionLoader extends BaseRegionLoader {

    public RegionLoader(final LevelProvider level, final int regionX, final int regionZ) throws IOException {
        super(level, regionX, regionZ, "mca");
    }

    protected static int getChunkOffset(final int x, final int z) {
        return x | z << 5;
    }

    @Override
    public Chunk readChunk(final int x, final int z) throws IOException {
        final int index = RegionLoader.getChunkOffset(x, z);
        if (index < 0 || index >= 4096) {
            return null;
        }

        this.lastUsed = System.currentTimeMillis();

        if (!this.isChunkGenerated(index)) {
            return null;
        }

        try {
            final Integer[] table = this.locationTable.get(index);
            final RandomAccessFile raf = this.getRandomAccessFile();
            raf.seek(table[0] << 12);
            final int length = raf.readInt();
            final byte compression = raf.readByte();
            if (length <= 0 || length >= BaseRegionLoader.MAX_SECTOR_LENGTH) {
                if (length >= BaseRegionLoader.MAX_SECTOR_LENGTH) {
                    table[0] = ++this.lastSector;
                    table[1] = 1;
                    this.locationTable.put(index, table);
                    MainLogger.getLogger().error("Corrupted chunk header detected");
                }
                return null;
            }

            if (length > table[1] << 12) {
                MainLogger.getLogger().error("Corrupted bigger chunk detected");
                table[1] = length >> 12;
                this.locationTable.put(index, table);
                this.writeLocationIndex(index);
            } else if (compression != BaseRegionLoader.COMPRESSION_ZLIB && compression != BaseRegionLoader.COMPRESSION_GZIP) {
                MainLogger.getLogger().error("Invalid compression type");
                return null;
            }

            final byte[] data = new byte[length - 1];
            raf.readFully(data);
            final Chunk chunk = this.unserializeChunk(data);
            if (chunk != null) {
                return chunk;
            } else {
                MainLogger.getLogger().error("Corrupted chunk detected at (" + x + ", " + z + ") in " + this.levelProvider.getName());
                return null;
            }
        } catch (final EOFException e) {
            MainLogger.getLogger().error("Your world is corrupt, because some code is bad and corrupted it. oops. ");
            return null;
        }
    }

    @Override
    public boolean chunkExists(final int x, final int z) {
        return this.isChunkGenerated(RegionLoader.getChunkOffset(x, z));
    }

    @Override
    public void removeChunk(final int x, final int z) {
        final int index = RegionLoader.getChunkOffset(x, z);
        final Integer[] table = this.locationTable.get(0);
        table[0] = 0;
        table[1] = 0;
        this.locationTable.put(index, table);
    }

    @Override
    public void writeChunk(final FullChunk chunk) throws Exception {
        this.lastUsed = System.currentTimeMillis();
        final byte[] chunkData = chunk.toBinary();
        this.saveChunk(chunk.getX() & 0x1f, chunk.getZ() & 0x1f, chunkData);
    }

    @Override
    public void close() throws IOException {
        this.writeLocationTable();
        this.levelProvider = null;
        super.close();
    }

    @Override
    public int doSlowCleanUp() throws Exception {
        final RandomAccessFile raf = this.getRandomAccessFile();
        for (int i = 0; i < 1024; i++) {
            Integer[] table = this.locationTable.get(i);
            if (table[0] == 0 || table[1] == 0) {
                continue;
            }
            raf.seek(table[0] << 12);
            byte[] chunk = new byte[table[1] << 12];
            raf.readFully(chunk);
            final int length = Binary.readInt(Arrays.copyOfRange(chunk, 0, 3));
            if (length <= 1) {
                this.locationTable.put(i, table = new Integer[]{0, 0, 0});
            }
            try {
                chunk = Zlib.inflate(Arrays.copyOf(chunk, 5));
            } catch (final Exception e) {
                this.locationTable.put(i, new Integer[]{0, 0, 0});
                continue;
            }
            chunk = Zlib.deflate(chunk, 9);
            final ByteBuffer buffer = ByteBuffer.allocate(4 + 1 + chunk.length);
            buffer.put(Binary.writeInt(chunk.length + 1));
            buffer.put(BaseRegionLoader.COMPRESSION_ZLIB);
            buffer.put(chunk);
            chunk = buffer.array();
            final int sectors = (int) Math.ceil(chunk.length / 4096d);
            if (sectors > table[1]) {
                table[0] = this.lastSector + 1;
                this.lastSector += sectors;
                this.locationTable.put(i, table);
            }
            raf.seek(table[0] << 12);
            final byte[] bytes = new byte[sectors << 12];
            final ByteBuffer buffer1 = ByteBuffer.wrap(bytes);
            buffer1.put(chunk);
            raf.write(buffer1.array());
        }
        this.writeLocationTable();
        final int n = this.cleanGarbage();
        this.writeLocationTable();
        return n;
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getZ() {
        return this.z;
    }

    @Override
    protected boolean isChunkGenerated(final int index) {
        final Integer[] array = this.locationTable.get(index);
        return !(array[0] == 0 || array[1] == 0);
    }

    @Override
    protected Chunk unserializeChunk(final byte[] data) {
        return Chunk.fromBinary(data, this.levelProvider);
    }

    @Override
    protected void saveChunk(final int x, final int z, final byte[] chunkData) throws IOException {
        final int length = chunkData.length + 1;
        if (length + 4 > BaseRegionLoader.MAX_SECTOR_LENGTH) {
            throw new ChunkException("Chunk is too big! " + (length + 4) + " > " + BaseRegionLoader.MAX_SECTOR_LENGTH);
        }
        final int sectors = (int) Math.ceil((length + 4) / 4096d);
        final int index = RegionLoader.getChunkOffset(x, z);
        boolean indexChanged = false;
        final Integer[] table = this.locationTable.get(index);

        if (table[1] < sectors) {
            table[0] = this.lastSector + 1;
            this.locationTable.put(index, table);
            this.lastSector += sectors;
            indexChanged = true;
        } else if (table[1] != sectors) {
            indexChanged = true;
        }

        table[1] = sectors;
        table[2] = (int) (System.currentTimeMillis() / 1000d);

        this.locationTable.put(index, table);
        final RandomAccessFile raf = this.getRandomAccessFile();
        raf.seek(table[0] << 12);

        final BinaryStream stream = new BinaryStream();
        stream.put(Binary.writeInt(length));
        stream.putByte(BaseRegionLoader.COMPRESSION_ZLIB);
        stream.put(chunkData);
        byte[] data = stream.getBuffer();
        if (data.length < sectors << 12) {
            final byte[] newData = new byte[sectors << 12];
            System.arraycopy(data, 0, newData, 0, data.length);
            data = newData;
        }

        raf.write(data);

        if (indexChanged) {
            this.writeLocationIndex(index);
        }
    }

    @Override
    protected void loadLocationTable() throws IOException {
        final RandomAccessFile raf = this.getRandomAccessFile();
        raf.seek(0);
        this.lastSector = 1;
        final int[] data = new int[1024 * 2]; //1024 records * 2 times
        for (int i = 0; i < 1024 * 2; i++) {
            data[i] = raf.readInt();
        }
        for (int i = 0; i < 1024; ++i) {
            final int index = data[i];
            this.locationTable.put(i, new Integer[]{index >> 8, index & 0xff, data[1024 + i]});
            final int value = this.locationTable.get(i)[0] + this.locationTable.get(i)[1] - 1;
            if (value > this.lastSector) {
                this.lastSector = value;
            }
        }
    }

    @Override
    protected void writeLocationIndex(final int index) throws IOException {
        final RandomAccessFile raf = this.getRandomAccessFile();
        final Integer[] array = this.locationTable.get(index);
        raf.seek(index << 2);
        raf.writeInt(array[0] << 8 | array[1]);
        raf.seek(4096 + (index << 2));
        raf.writeInt(array[2]);
    }

    @Override
    protected void createBlank() throws IOException {
        final RandomAccessFile raf = this.getRandomAccessFile();
        raf.seek(0);
        raf.setLength(0);
        this.lastSector = 1;
        final int time = (int) (System.currentTimeMillis() / 1000d);
        for (int i = 0; i < 1024; ++i) {
            this.locationTable.put(i, new Integer[]{0, 0, time});
            raf.writeInt(0);
        }
        for (int i = 0; i < 1024; ++i) {
            raf.writeInt(time);
        }
    }

    private void writeLocationTable() throws IOException {
        final RandomAccessFile raf = this.getRandomAccessFile();
        raf.seek(0);
        for (int i = 0; i < 1024; ++i) {
            final Integer[] array = this.locationTable.get(i);
            raf.writeInt(array[0] << 8 | array[1]);
        }
        for (int i = 0; i < 1024; ++i) {
            final Integer[] array = this.locationTable.get(i);
            raf.writeInt(array[2]);
        }
    }

    private int cleanGarbage() throws IOException {
        final RandomAccessFile raf = this.getRandomAccessFile();
        final Map<Integer, Integer> sectors = new TreeMap<>();
        for (final Map.Entry entry : this.locationTable.entrySet()) {
            final Integer index = (Integer) entry.getKey();
            final Integer[] data = (Integer[]) entry.getValue();
            if (data[0] == 0 || data[1] == 0) {
                this.locationTable.put(index, new Integer[]{0, 0, 0});
                continue;
            }
            sectors.put(data[0], index);
        }

        if (sectors.size() == this.lastSector - 2) {
            return 0;
        }
        int shift = 0;
        final int lastSector = 1;

        raf.seek(8192);
        int s = 2;
        for (final int sector : sectors.keySet()) {
            s = sector;
            final int index = sectors.get(sector);
            if (sector - lastSector > 1) {
                shift += sector - lastSector - 1;
            }
            if (shift > 0) {
                raf.seek(sector << 12);
                final byte[] old = new byte[4096];
                raf.readFully(old);
                raf.seek(sector - shift << 12);
                raf.write(old);
            }
            final Integer[] v = this.locationTable.get(index);
            v[0] -= shift;
            this.locationTable.put(index, v);
            this.lastSector = sector;
        }
        raf.setLength(s + 1 << 12);
        return shift;
    }

}
