package cn.nukkit.level.format.anvil;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.anvil.palette.DataPalette;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ChunkSection implements cn.nukkit.level.format.ChunkSection {

    private final int y;

    private DataPalette palette;

    private byte[] blockLight;
    private byte[] skyLight;

    public ChunkSection(int y) {
        this.y = y;

        this.blockLight = new byte[2048];
        this.skyLight = new byte[2048];

        palette = new DataPalette();
    }

    public ChunkSection(CompoundTag nbt) {
        this.y = nbt.getByte("Y");

        byte[] blocks = nbt.getByteArray("Blocks");
        byte[] data = nbt.getByteArray("Data");

        char[] rawData = new char[4096];
        int index = 0;
        int i1, i2, i3, i4;
        for (int x = 0; x < 16;) {
            {
                i1 = x;
                for (int z = 0; z < 16; z++) {
                    i2 = i1 + (z << 4);
                    for (int y = 0; y < 16; y += 2, index += 2) {
                        i3 = i2 + (y << 8);
                        i4 = i3 + 256;
                        char val1 = (char) (((blocks[i3] & 0xFF) << 4) | (data[i3 >> 1] & 0xF));
                        char val2 = (char) (((blocks[i4] & 0xFF) << 4) | (data[i4 >> 1] & 0xF));
                        rawData[index] = val1;
                        rawData[index + 1] = val2;
                    }
                }
            }
            x++;
            {
                i1 = x;
                for (int z = 0; z < 16; z++) {
                    i2 = i1 + (z << 4);
                    for (int y = 0; y < 16; y += 2, index += 2) {
                        i3 = i2 + (y << 8);
                        i4 = i3 + 256;
                        char val1 = (char) (((blocks[i3] & 0xFF) << 4) | ((data[i3 >> 1] & 0xF0) >> 4));
                        char val2 = (char) (((blocks[i4] & 0xFF) << 4) | ((data[i4 >> 1] & 0xF0) >> 4));
                        rawData[index] = val1;
                        rawData[index + 1] = val2;
                    }
                }
            }
            x++;
        }

        palette = new DataPalette(rawData);

        this.blockLight = nbt.getByteArray("BlockLight");
        this.skyLight = nbt.getByteArray("SkyLight");
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getBlockId(int x, int y, int z) {
        return palette.getFullBlock(x, y, z) >> 4;
    }

    @Override
    public void setBlockId(int x, int y, int z, int id) {
        palette.setFullBlock(x, y, z, (char) (id << 4));
    }

    @Override
    public int getBlockData(int x, int y, int z) {
        return palette.getBlockData(x, y, z);
    }

    @Override
    public void setBlockData(int x, int y, int z, int data) {
        palette.setBlockData(x, y, z, data);
    }

    @Override
    public int getFullBlock(int x, int y, int z) {
        return palette.getFullBlock(x, y, z);
    }

    @Override
    public boolean setBlock(int x, int y, int z, int blockId) {
        return setBlock(x, y, z, blockId, 0);
    }

    public Block getAndSetBlock(int x, int y, int z, Block block) {
        int fullId = palette.getAndSetFullBlock(x, y, z, block.getFullId());
        return Block.fullList[fullId].clone();
    }

    @Override
    public boolean setBlock(int x, int y, int z, int blockId, int meta) {
        int newFullId = (blockId << 4) + meta;
        int previousFullId = palette.getAndSetFullBlock(x, y, z, newFullId);
        return (newFullId != previousFullId);
    }

    @Override
    public int getBlockSkyLight(int x, int y, int z) {
        int sl = this.skyLight[(y << 7) | (z << 3) | (x >> 1)] & 0xff;
        if ((x & 1) == 0) {
            return sl & 0x0f;
        }
        return sl >> 4;
    }

    @Override
    public void setBlockSkyLight(int x, int y, int z, int level) {
        int i = (y << 7) | (z << 3) | (x >> 1);
        int old = this.skyLight[i] & 0xff;
        if ((x & 1) == 0) {
            this.skyLight[i] = (byte) ((old & 0xf0) | (level & 0x0f));
        } else {
            this.skyLight[i] = (byte) (((level & 0x0f) << 4) | (old & 0x0f));
        }
    }

    @Override
    public int getBlockLight(int x, int y, int z) {
        int l = this.blockLight[(y << 7) | (z << 3) | (x >> 1)] & 0xff;
        if ((x & 1) == 0) {
            return l & 0x0f;
        }
        return l >> 4;
    }

    @Override
    public void setBlockLight(int x, int y, int z, int level) {
        int i = (y << 7) | (z << 3) | (x >> 1);
        int old = this.blockLight[i] & 0xff;
        if ((x & 1) == 0) {
            this.blockLight[i] = (byte) ((old & 0xf0) | (level & 0x0f));
        } else {
            this.blockLight[i] = (byte) (((level & 0x0f) << 4) | (old & 0x0f));
        }
    }

    private static final ThreadLocal<byte[][]> idArray = new ThreadLocal<byte[][]>() {
        @Override
        protected byte[][] initialValue() {
            return new byte[16][];
        }
    };

    private static final ThreadLocal<byte[][]> dataArray = new ThreadLocal<byte[][]>() {
        @Override
        protected byte[][] initialValue() {
            return new byte[16][];
        }
    };

    @Override
    public byte[] getIdArray() {
        char[] raw = palette.getRaw();
        byte[][] bufferLayers = idArray.get();
        byte[] buffer = bufferLayers[y];
        if (buffer == null) buffer = bufferLayers[y] = new byte[4096];

        int srcIndex = 0;
        for (int x = 0; x < 16; x++) {
            int destIndexX = x;
            for (int z = 0; z < 16; z++) {
                int destIndexZ = destIndexX + (z << 4);
                for (int y = 0; y < 16; y++, srcIndex++) {
                    int destIndex = destIndexZ + (y << 8);
                    buffer[destIndex] = (byte) (raw[srcIndex] >> 4);
                }
            }
        }

        return buffer;

    }

    @Override
    public byte[] getDataArray() {
        char[] raw = palette.getRaw();
        byte[][] bufferLayers = dataArray.get();
        byte[] buffer = bufferLayers[y];
        if (buffer == null) buffer = bufferLayers[y] = new byte[2048];

        int srcIndex = 0;
        for (int x = 0; x < 16; x++) {
            int destIndexX = x;
            for (int z = 0; z < 16; z++) {
                int destIndexZ = destIndexX + (z << 4);
                for (int y = 0; y < 16; y += 2, srcIndex ++) {
                    int destIndex1 = destIndexZ + (y << 8);
                    int destIndex2 = destIndex1 + 256;

                    byte newVal = (byte) ((raw[destIndex1] & 0xF) + ((raw[destIndex2] & 0xF) << 4));

                    buffer[srcIndex] = newVal;
                }
            }
        }

        return buffer;
    }

    @Override
    public byte[] getSkyLightArray() {
        return this.skyLight;
    }

    @Override
    public byte[] getLightArray() {
        return this.blockLight;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    private static final ThreadLocal<byte[]> toXZYBuffer = new ThreadLocal<byte[]>() {
        @Override
        protected byte[] initialValue() {
            return new byte[6144];
        }
    };

    private byte[] toXYZ(char[] raw) {
        byte[] buffer = toXZYBuffer.get();
        for (int i = 0; i < 4096; i++) {
            buffer[i] = (byte) (raw[i] >> 4);
        }
        for (int i = 0, j = 4096; i < 2048; i += 2, j++) {
            buffer[j] = (byte) ((raw[i] & 0xF) + ((raw[i + 1] & 0xF) << 4));
        }
        return buffer;
    }

    @Override
    public byte[] getBytes() {
        return toXYZ(palette.getRaw());
    }

    public boolean compress() {
        return palette.compress();
    }

    @Override
    public ChunkSection clone() {
        ChunkSection section;
        try {
            section = (ChunkSection) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
        section.skyLight = this.skyLight.clone();
        section.blockLight = this.blockLight.clone();
        section.palette = this.palette.clone();
        return section;
    }
}
