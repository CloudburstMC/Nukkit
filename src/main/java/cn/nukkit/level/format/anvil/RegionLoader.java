package cn.nukkit.level.format.anvil;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.generic.BaseRegionLoader;
import cn.nukkit.utils.*;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import lombok.extern.log4j.Log4j2;

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Log4j2
public class RegionLoader extends BaseRegionLoader {
    public RegionLoader(LevelProvider level, int regionX, int regionZ) throws IOException {
        super(level, regionX, regionZ, "mca");
    }

    @Override
    protected boolean isChunkGenerated(int index) {
        int[] array = this.primitiveLocationTable.get(index);
        return !(array[0] == 0 || array[1] == 0);
    }

    @Override
    public Chunk readChunk(int x, int z) throws IOException {
        int index = getChunkOffset(x, z);
        if (index < 0 || index >= 4096) {
            return null;
        }

        this.lastUsed = System.currentTimeMillis();

        if (!this.isChunkGenerated(index)) {
            return null;
        }

        try {
            int[] table = this.primitiveLocationTable.get(index);
            RandomAccessFile raf = this.getRandomAccessFile();
            raf.seek((long)table[0] << 12L);
            int length = raf.readInt();
            byte compression = raf.readByte();
            if (length <= 0 || length >= MAX_SECTOR_LENGTH) {
                if (length >= MAX_SECTOR_LENGTH) {
                    table[0] = ++this.lastSector;
                    table[1] = 1;
                    this.primitiveLocationTable.put(index, table);
                    log.error("Corrupted chunk header detected");
                }
                return null;
            }
    
            if (length > (table[1] << 12)) {
                log.error("Corrupted bigger chunk detected");
                table[1] = length >> 12;
                this.primitiveLocationTable.put(index, table);
                this.writeLocationIndex(index);
            } else if (compression != COMPRESSION_ZLIB && compression != COMPRESSION_GZIP) {
                log.error("Invalid compression type");
                return null;
            }
    
            byte[] data = new byte[length - 1];
            raf.readFully(data);
            Chunk chunk = this.unserializeChunk(data);
            if (chunk != null) {
                return chunk;
            } else {
                log.error("Corrupted chunk detected at ({}, {}) in {}", x, z, levelProvider.getName());
                return null;
            }
        } catch (EOFException e) {
            log.error("Your world is corrupt, because some code is bad and corrupted it. oops. ");
            return null;
        }
    }

    @Override
    protected Chunk unserializeChunk(byte[] data) {
        return Chunk.fromBinary(data, this.levelProvider);
    }

    @Override
    public boolean chunkExists(int x, int z) {
        return this.isChunkGenerated(getChunkOffset(x, z));
    }

    @Override
    protected void saveChunk(int x, int z, byte[] chunkData) throws IOException {
        int length = chunkData.length + 1;
        if (length + 4 > MAX_SECTOR_LENGTH) {
            throw new ChunkException("Chunk is too big! " + (length + 4) + " > " + MAX_SECTOR_LENGTH);
        }
        int sectors = (int) Math.ceil((length + 4) / 4096d);
        int index = getChunkOffset(x, z);
        boolean indexChanged = false;
        int[] table = this.primitiveLocationTable.get(index);

        if (table[1] < sectors) {
            table[0] = this.lastSector + 1;
            this.primitiveLocationTable.put(index, table);
            this.lastSector += sectors;
            indexChanged = true;
        } else if (table[1] != sectors) {
            indexChanged = true;
        }

        table[1] = sectors;
        table[2] = (int) (System.currentTimeMillis() / 1000d);

        this.primitiveLocationTable.put(index, table);
        RandomAccessFile raf = this.getRandomAccessFile();
        raf.seek((long)table[0] << 12L);

        BinaryStream stream = new BinaryStream();
        stream.put(Binary.writeInt(length));
        stream.putByte(COMPRESSION_ZLIB);
        stream.put(chunkData);
        byte[] data = stream.getBuffer();
        if (data.length < sectors << 12) {
            byte[] newData = new byte[sectors << 12];
            System.arraycopy(data, 0, newData, 0, data.length);
            data = newData;
        }

        raf.write(data);

        if (indexChanged) {
            this.writeLocationIndex(index);
        }
    }

    @Override
    public void removeChunk(int x, int z) {
        int index = getChunkOffset(x, z);
        int[] table = this.primitiveLocationTable.get(0);
        table[0] = 0;
        table[1] = 0;
        this.primitiveLocationTable.put(index, table);
    }

    @Override
    public void writeChunk(FullChunk chunk) throws Exception {
        this.lastUsed = System.currentTimeMillis();
        byte[] chunkData = chunk.toBinary();
        this.saveChunk(chunk.getX() & 0x1f, chunk.getZ() & 0x1f, chunkData);
    }

    protected static int getChunkOffset(int x, int z) {
        return x | (z << 5);
    }

    @Override
    public void close() throws IOException {
        this.writeLocationTable();
        this.levelProvider = null;
        super.close();
    }

    @Override
    public int doSlowCleanUp() throws Exception {
        RandomAccessFile raf = this.getRandomAccessFile();
        for (int i = 0; i < 1024; i++) {
            int[] table = this.primitiveLocationTable.get(i);
            if (table[0] == 0 || table[1] == 0) {
                continue;
            }
            raf.seek((long)table[0] << 12L);
            byte[] chunk = new byte[table[1] << 12];
            raf.readFully(chunk);
            int length = Binary.readInt(Arrays.copyOfRange(chunk, 0, 3));
            if (length <= 1) {
                this.primitiveLocationTable.put(i, (table = new int[]{0, 0, 0}));
            }
            try {
                chunk = Zlib.inflate(Arrays.copyOf(chunk, 5));
            } catch (Exception e) {
                this.primitiveLocationTable.put(i, new int[]{0, 0, 0});
                continue;
            }
            chunk = Zlib.deflate(chunk, 9);
            ByteBuffer buffer = ByteBuffer.allocate(4 + 1 + chunk.length);
            buffer.put(Binary.writeInt(chunk.length + 1));
            buffer.put(COMPRESSION_ZLIB);
            buffer.put(chunk);
            chunk = buffer.array();
            int sectors = (int) Math.ceil(chunk.length / 4096d);
            if (sectors > table[1]) {
                table[0] = this.lastSector + 1;
                this.lastSector += sectors;
                this.primitiveLocationTable.put(i, table);
            }
            raf.seek((long)table[0] << 12L);
            byte[] bytes = new byte[sectors << 12];
            ByteBuffer buffer1 = ByteBuffer.wrap(bytes);
            buffer1.put(chunk);
            raf.write(buffer1.array());
        }
        this.writeLocationTable();
        int n = this.cleanGarbage();
        this.writeLocationTable();
        return n;
    }

    @Override
    protected void loadLocationTable() throws IOException {
        RandomAccessFile raf = this.getRandomAccessFile();
        raf.seek(0);
        this.lastSector = 1;
        int[] data = new int[1024 * 2]; //1024 records * 2 times
        for (int i = 0; i < 1024 * 2; i++) {
            data[i] = raf.readInt();
        }
        for (int i = 0; i < 1024; ++i) {
            int index = data[i];
            this.primitiveLocationTable.put(i, new int[]{index >> 8, index & 0xff, data[1024 + i]});
            int value = this.primitiveLocationTable.get(i)[0] + this.primitiveLocationTable.get(i)[1] - 1;
            if (value > this.lastSector) {
                this.lastSector = value;
            }
        }
    }

    private void writeLocationTable() throws IOException {
        RandomAccessFile raf = this.getRandomAccessFile();
        raf.seek(0);
        for (int i = 0; i < 1024; ++i) {
            int[] array = this.primitiveLocationTable.get(i);
            raf.writeInt((array[0] << 8) | array[1]);
        }
        for (int i = 0; i < 1024; ++i) {
            int[] array = this.primitiveLocationTable.get(i);
            raf.writeInt(array[2]);
        }
    }

    private int cleanGarbage() throws IOException {
        RandomAccessFile raf = this.getRandomAccessFile();
        Map<Integer, Integer> sectors = new TreeMap<>();
        for (Int2ObjectMap.Entry<int[]> entry : this.primitiveLocationTable.int2ObjectEntrySet()) {
            int index = entry.getIntKey();
            int[] data = entry.getValue();
            if (data[0] == 0 || data[1] == 0) {
                this.primitiveLocationTable.put(index, new int[]{0, 0, 0});
                continue;
            }
            sectors.put(data[0], index);
        }

        if (sectors.size() == (this.lastSector - 2)) {
            return 0;
        }
        int shift = 0;
        int lastSector = 1;

        raf.seek(8192);
        int s = 2;
        for (int sector : sectors.keySet()) {
            s = sector;
            int index = sectors.get(sector);
            if ((sector - lastSector) > 1) {
                shift += sector - lastSector - 1;
            }
            if (shift > 0) {
                raf.seek((long)sector << 12L);
                byte[] old = new byte[4096];
                raf.readFully(old);
                raf.seek((long)(sector - shift) << 12L);
                raf.write(old);
            }
            int[] v = this.primitiveLocationTable.get(index);
            v[0] -= shift;
            this.primitiveLocationTable.put(index, v);
            this.lastSector = sector;
        }
        raf.setLength((s + 1) << 12);
        return shift;
    }

    @Override
    protected void writeLocationIndex(int index) throws IOException {
        RandomAccessFile raf = this.getRandomAccessFile();
        int[] array = this.primitiveLocationTable.get(index);
        raf.seek(index << 2);
        raf.writeInt((array[0] << 8) | array[1]);
        raf.seek(4096 + (index << 2));
        raf.writeInt(array[2]);
    }

    @Override
    protected void createBlank() throws IOException {
        RandomAccessFile raf = this.getRandomAccessFile();
        raf.seek(0);
        raf.setLength(0);
        this.lastSector = 1;
        int time = (int) (System.currentTimeMillis() / 1000d);
        for (int i = 0; i < 1024; ++i) {
            this.primitiveLocationTable.put(i, new int[]{0, 0, time});
            raf.writeInt(0);
        }
        for (int i = 0; i < 1024; ++i) {
            raf.writeInt(time);
        }
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getZ() {
        return z;
    }
}
