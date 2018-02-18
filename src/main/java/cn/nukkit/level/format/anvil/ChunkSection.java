package cn.nukkit.level.format.anvil;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.anvil.palette.BlockDataPalette;
import cn.nukkit.level.format.generic.EmptyChunkSection;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.PlayerProtocol;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.ThreadCache;
import cn.nukkit.utils.Utils;
import cn.nukkit.utils.Zlib;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ChunkSection implements cn.nukkit.level.format.ChunkSection {

    private final int y;

    private BlockDataPalette palette;

    protected byte[] blockLight;
    protected byte[] skyLight;
    protected byte[] compressedLight;
    protected boolean hasBlockLight;
    protected boolean hasSkyLight;

    public ChunkSection(int y) {
        this.y = y;

        hasBlockLight = false;
        hasSkyLight = false;

        palette = new BlockDataPalette();
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

        palette = new BlockDataPalette(rawData);

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
    public boolean setFullBlockId(int x, int y, int z, int fullId) {
        palette.setFullBlock(x, y, z, (char) fullId);
        return true;
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
        if (this.skyLight == null) {
            if (!hasSkyLight) {
                return 0;
            } else if (compressedLight == null) {
                return 15;
            }
        }
        this.skyLight = getSkyLightArray();
        int sl = this.skyLight[(y << 7) | (z << 3) | (x >> 1)] & 0xff;
        if ((x & 1) == 0) {
            return sl & 0x0f;
        }
        return sl >> 4;
    }

    @Override
    public void setBlockSkyLight(int x, int y, int z, int level) {
        if (this.skyLight == null) {
            if (hasSkyLight && compressedLight != null) {
                this.skyLight = getSkyLightArray();
            } else if (level == (hasSkyLight ? 15 : 0)) {
                return;
            } else {
                this.skyLight = new byte[2048];
                if (hasSkyLight) {
                    Arrays.fill(this.skyLight, (byte) 0xFF);
                }
            }
        }
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
        if (blockLight == null && !hasBlockLight) return 0;
        this.blockLight = getLightArray();
        int l = blockLight[(y << 7) | (z << 3) | (x >> 1)] & 0xff;
        if ((x & 1) == 0) {
            return l & 0x0f;
        }
        return l >> 4;
    }

    @Override
    public void setBlockLight(int x, int y, int z, int level) {
        if (this.blockLight == null) {
            if (hasBlockLight) {
                this.blockLight = getLightArray();
            } else if (level == 0) {
                return;
            } else {
                this.blockLight = new byte[2048];
            }
        }
        int i = (y << 7) | (z << 3) | (x >> 1);
        int old = this.blockLight[i] & 0xff;
        if ((x & 1) == 0) {
            this.blockLight[i] = (byte) ((old & 0xf0) | (level & 0x0f));
        } else {
            this.blockLight[i] = (byte) (((level & 0x0f) << 4) | (old & 0x0f));
        }
    }

    @Override
    public byte[] getIdArray() {
        char[] raw = palette.getRaw();
        byte[][] bufferLayers = ThreadCache.idArray.get();
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
        byte[][] bufferLayers = ThreadCache.dataArray.get();
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
        if (this.skyLight != null) return skyLight;
        if (hasSkyLight) {
            if (compressedLight != null) {
                inflate();
                return this.skyLight;
            }
            return EmptyChunkSection.EMPTY_SKY_LIGHT_ARR;
        } else {
            return EmptyChunkSection.EMPTY_LIGHT_ARR;
        }
    }

    private void inflate() {
        try {
            if (compressedLight != null && compressedLight.length != 0) {
                byte[] inflated = Zlib.inflate(compressedLight);
                blockLight = Arrays.copyOfRange(inflated, 0, 2048);
                if (inflated.length > 2048) {
                    skyLight = Arrays.copyOfRange(inflated, 2048, 4096);
                } else {
                    skyLight = new byte[2048];
                    if (hasSkyLight) {
                        Arrays.fill(skyLight, (byte) 0xFF);
                    }
                }
                compressedLight = null;
            } else {
                blockLight = new byte[2048];
                skyLight = new byte[2048];
                if (hasSkyLight) {
                    Arrays.fill(skyLight, (byte) 0xFF);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] getLightArray() {
        if (this.blockLight != null) return blockLight;
        if (hasBlockLight) {
            inflate();
            return this.blockLight;
        } else {
            return EmptyChunkSection.EMPTY_LIGHT_ARR;
        }
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    private byte[] toXZY(char[] raw) {
        byte[] buffer = ThreadCache.byteCache6144.get();
        for (int i = 0; i < 4096; i++) {
            buffer[i] = (byte) (raw[i] >> 4);
        }
        for (int i = 0, j = 4096; i < 4096; i += 2, j++) {
            buffer[j] = (byte) (((raw[i + 1] & 0xF) << 4) | (raw[i] & 0xF));
        }
        return buffer;
    }

    @Override
    public byte[] getBytes(PlayerProtocol protocol) {
        if (protocol.getMainNumber() >= 130) return toXZY(palette.getRaw());
        ByteBuffer buffer;
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
        buffer = ByteBuffer.allocate(10240);
        return buffer
                .put(blocks)
                .put(data)
                .put(skyLight)
                .put(blockLight)
                .array();
    }

    public boolean compress() {
        if (!palette.compress()) {
            if (blockLight != null) {
                byte[] arr1 = blockLight;
                hasBlockLight = !Utils.isByteArrayEmpty(arr1);
                byte[] arr2;
                if (skyLight != null) {
                    arr2 = skyLight;
                    hasSkyLight = !Utils.isByteArrayEmpty(arr2);
                } else if (hasSkyLight) {
                    arr2 = EmptyChunkSection.EMPTY_SKY_LIGHT_ARR;
                } else {
                    arr2 = EmptyChunkSection.EMPTY_LIGHT_ARR;
                    hasSkyLight = false;
                }
                blockLight = null;
                skyLight = null;
                byte[] toDeflate = null;
                if (hasBlockLight && hasSkyLight && arr2 != EmptyChunkSection.EMPTY_SKY_LIGHT_ARR) {
                    toDeflate = Binary.appendBytes(arr1, arr2);
                } else if (hasBlockLight) {
                    toDeflate = arr1;
                }
                if (toDeflate != null) {
                    try {
                        compressedLight = Zlib.deflate(toDeflate, 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
            return false;
        }
        return true;
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
        if (this.blockLight != null) section.blockLight = this.blockLight.clone();
        if (this.skyLight != null) section.skyLight = this.skyLight.clone();
        section.hasBlockLight = this.hasBlockLight;
        section.hasSkyLight = this.hasSkyLight;
        if (this.compressedLight != null) section.compressedLight = this.compressedLight.clone();
        section.palette = this.palette.clone();
        return section;
    }
}
