package cn.nukkit.level.format.anvil;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ChunkSection implements cn.nukkit.level.format.ChunkSection {

    private final int y;
    private byte[] blocks;
    private byte[] data;
    private byte[] blockLight;
    private byte[] skyLight;

    public ChunkSection(int y) {
        this.y = y;
        this.blocks = new byte[4096];
        this.data = new byte[2048];
        this.blockLight = new byte[2048];
        this.skyLight = new byte[2048];
    }

    public ChunkSection(CompoundTag nbt) {
        this.y = nbt.getByte("Y");
        this.blocks = nbt.getByteArray("Blocks");
        this.data = nbt.getByteArray("Data");
        this.blockLight = nbt.getByteArray("BlockLight");
        this.skyLight = nbt.getByteArray("SkyLight");
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getBlockId(int x, int y, int z) {
        return this.blocks[(y << 8) | (z << 4) | x] & 0xff;
    }

    @Override
    public void setBlockId(int x, int y, int z, int id) {
        this.blocks[(y << 8) | (z << 4) | x] = (byte) id;
    }

    @Override
    public int getBlockData(int x, int y, int z) {
        int b = this.data[(y << 7) | (z << 3) | (x >> 1)] & 0xff;
        if ((x & 1) == 0) {
            return b & 0x0f;
        }
        return b >> 4;
    }

    @Override
    public void setBlockData(int x, int y, int z, int data) {
        int i = (y << 7) | (z << 3) | (x >> 1);
        int old = this.data[i] & 0xff;
        if ((x & 1) == 0) {
            this.data[i] = (byte) ((old & 0xf0) | (data & 0x0f));
        } else {
            this.data[i] = (byte) (((data & 0x0f) << 4) | (old & 0x0f));
        }
    }

    @Override
    public int getFullBlock(int x, int y, int z) {
        int i = (y << 8) | (z << 4) | x;
        int block = this.blocks[i] & 0xff;
        int data = this.data[i >> 1] & 0xff;
        if ((x & 1) == 0) {
            return (block << 4) | (data & 0x0f);
        }
        return (block << 4) | (data >> 4);
    }

    @Override
    public boolean setBlock(int x, int y, int z) {
        return setBlock(x, y, z, null, null);
    }

    @Override
    public boolean setBlock(int x, int y, int z, Integer blockId) {
        return setBlock(x, y, z, blockId, null);
    }

    @Override
    public boolean setBlock(int x, int y, int z, Integer blockId, Integer meta) {
        int i = (y << 8) | (z << 4) | x;
        boolean changed = false;
        if (blockId != null) {
            byte id = blockId.byteValue();
            if (this.blocks[i] != id) {
                this.blocks[i] = id;
                changed = true;
            }
        }

        if (meta != null) {
            i >>= 1;
            int old = this.data[i] & 0xff;
            if ((x & 1) == 0) {
                this.data[i] = (byte) ((old & 0xf0) | (meta & 0x0f));
                if (!meta.equals(old & 0x0f)) {
                    changed = true;
                }
            } else {
                this.data[i] = (byte) (((meta & 0x0f) << 4) | (old & 0x0f));
                if (!meta.equals(old >> 4)) {
                    changed = true;
                }
            }
        }

        return changed;
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

    @Override
    public byte[] getBlockIdColumn(int x, int z) {
        int i = (z << 4) | x;
        byte[] column = new byte[16];
        for (int y = 0; y < 16; y++) {
            column[y] = this.blocks[(y << 8) | i];
        }
        return column;
    }

    @Override
    public byte[] getBlockDataColumn(int x, int z) {
        int i = (z << 3) | (x >> 1);
        ByteBuffer column = ByteBuffer.allocate(8);
        int data1 = this.data[(y << 7) | i] & 0xff;
        int data2 = this.data[((y + 1) << 7) | i] & 0xff;
        if ((x & 1) == 0) {
            for (int y = 0; y < 16; y += 2) {
                column.put((byte) ((data1 & 0x0f) | ((data2 & 0x0f) << 4)));
            }
        } else {
            for (int y = 0; y < 16; y += 2) {
                column.put((byte) ((data1 >> 4) | (data2 & 0xf0)));
            }
        }
        return column.array();
    }

    @Override
    public byte[] getBlockSkyLightColumn(int x, int z) {
        int i = (z << 3) | (x >> 1);
        ByteBuffer column = ByteBuffer.allocate(8);
        int skyLight1 = this.skyLight[(y << 7) | i] & 0xff;
        int skyLight2 = this.skyLight[((y + 1) << 7) | i] & 0xff;
        if ((x & 1) == 0) {
            for (int y = 0; y < 16; y += 2) {
                column.put((byte) ((skyLight1 & 0x0f) | ((skyLight2 & 0x0f) << 4)));
            }
        } else {
            for (int y = 0; y < 16; y += 2) {
                column.put((byte) ((skyLight1 >> 4) | (skyLight2 & 0xf0)));
            }
        }
        return column.array();
    }

    @Override
    public byte[] getBlockLightColumn(int x, int z) {
        int i = (z << 3) | (x >> 1);
        ByteBuffer column = ByteBuffer.allocate(8);
        int blockLight1 = this.blockLight[(y << 7) | i] & 0xff;
        int blockLight2 = this.blockLight[((y + 1) << 7) | i] & 0xff;
        if ((x & 1) == 0) {
            for (int y = 0; y < 16; y += 2) {
                column.put((byte) ((blockLight1 & 0x0f) | ((blockLight2 & 0x0f) << 4)));
            }
        } else {
            for (int y = 0; y < 16; y += 2) {
                column.put((byte) ((blockLight1 >> 4) | (blockLight2 & 0xf0)));
            }
        }
        return column.array();
    }

    @Override
    public byte[] getIdArray() {
        return this.blocks;
    }

    @Override
    public byte[] getDataArray() {
        return this.data;
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
        return Utils.isByteArrayEmpty(this.blocks);
    }

    @Override
    public byte[] getBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(10240);
        byte[] blocks = new byte[4096];
        byte[] data = new byte[2048];
        byte[] skyLight = new byte[2048];
        byte[] blockLight = new byte[2048];
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int i = (x << 7) | (z << 3);
                for (int y = 0; y < 16; y += 2) {
                    blocks[(i << 1) | y] = (byte) this.getBlockId(x, y, z);
                    blocks[(i << 1) | (y + 1)] = (byte) this.getBlockId(x, y + 1, z);
                    int b1 = this.getBlockData(x, y, z);
                    int b2 = this.getBlockData(x, y + 1, z);
                    data[i | (y >> 1)] = (byte) ((b2 << 4) | b1);
                    b1 = this.getBlockSkyLight(x, y, z);
                    b2 = this.getBlockSkyLight(x, y + 1, z);
                    skyLight[i | (y >> 1)] = (byte) ((b2 << 4) | b1);
                    b1 = this.getBlockLight(x, y, z);
                    b2 = this.getBlockLight(x, y + 1, z);
                    blockLight[i | (y >> 1)] = (byte) ((b2 << 4) | b1);
                }
            }
        }
        return buffer
                .put(blocks)
                .put(data)
                .put(skyLight)
                .put(blockLight)
                .array();
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
        section.blocks = this.blocks.clone();
        section.data = this.data.clone();
        return section;
    }
}
