package cn.nukkit.level.format.leveldb.structure;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLayer;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.generic.EmptyChunkSection;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.Utils;
import cn.nukkit.utils.Zlib;
import lombok.Getter;

import java.io.IOException;
import java.util.Arrays;

public class LevelDBChunkSection implements ChunkSection {

    @Getter
    private final int y;
    @Getter
    private final StateBlockStorage[] storages;

    protected byte[] blockLight;
    protected byte[] skyLight;
    private byte[] compressedLight;
    protected boolean hasBlockLight;
    protected boolean hasSkyLight;

    public LevelDBChunkSection(int y, StateBlockStorage[] storages, byte[] blockLight, byte[] skyLight, byte[] compressedLight, boolean hasBlockLight, boolean hasSkyLight) {
        this.y = y;
        this.storages = storages;
        this.blockLight = blockLight;
        this.skyLight = skyLight;
        this.compressedLight = compressedLight;
        this.hasBlockLight = hasBlockLight;
        this.hasSkyLight = hasSkyLight;
    }

    public LevelDBChunkSection(int y) {
        this(y, new StateBlockStorage[]{new StateBlockStorage(), new StateBlockStorage()}, null, null, null, false, true);
    }

    private StateBlockStorage computeStorage(BlockLayer layer) {
        if (this.storages.length <= layer.ordinal()) {
            throw new IllegalArgumentException("Tried to get layer " + layer + " but LevelDBChunkSection has only " + this.storages.length);
        }

        StateBlockStorage storage = this.storages[layer.ordinal()];
        if (storage == null) {
            return this.storages[layer.ordinal()] = new StateBlockStorage();
        }
        return storage;
    }

    @Override
    public int getBlockId(int x, int y, int z, BlockLayer layer) {
        synchronized (this.storages) {
            return this.computeStorage(layer).getBlockId(x, y, z);
        }
    }

    @Override
    public void setBlockId(int x, int y, int z, BlockLayer layer, int id) {
        synchronized (this.storages) {
            this.computeStorage(layer).setBlockId(x, y, z, id);
            if (layer != Block.LAYER_WATERLOGGED && id == 0 && this.hasSecondLayer()) {
                this.computeStorage(Block.LAYER_WATERLOGGED).setBlockId(x, y, z, 0);
            }
        }
    }

    @Override
    public boolean setFullBlockId(int x, int y, int z, BlockLayer layer, int fullId) {
        synchronized (this.storages) {
            this.computeStorage(layer).setFullBlock(x, y, z, fullId);
            if (layer != Block.LAYER_WATERLOGGED && fullId == 0 && this.hasSecondLayer()) {
                this.computeStorage(Block.LAYER_WATERLOGGED).setFullBlock(x, y, z, 0);
            }
        }
        return true;
    }

    @Override
    public int getBlockData(int x, int y, int z, BlockLayer layer) {
        synchronized (this.storages) {
            return this.computeStorage(layer).getBlockData(x, y, z);
        }
    }

    @Override
    public void setBlockData(int x, int y, int z, BlockLayer layer, int data) {
        synchronized (this.storages) {
            this.computeStorage(layer).setBlockData(x, y, z, data);
        }
    }

    @Override
    public int getFullBlock(int x, int y, int z, BlockLayer layer) {
        synchronized (this.storages) {
            return this.computeStorage(layer).getFullBlock(x, y, z);
        }
    }

    public Block getAndSetBlock(int x, int y, int z, BlockLayer layer, Block block) {
        synchronized (this.storages) {
            int fullId = this.computeStorage(layer).getAndSetFullBlock(x, y, z, block.getFullId());
            if (layer != Block.LAYER_WATERLOGGED && block.getId() == 0 && fullId != block.getFullId() && this.hasSecondLayer()) {
                this.computeStorage(Block.LAYER_WATERLOGGED).setFullBlock(x, y, z, 0);
            }
            return Block.get(fullId, null, x, y, z, layer);
        }
    }

    @Override
    public boolean setBlock(int x, int y, int z, int blockId) {
        return this.setBlock(x, y, z, blockId, 0);
    }

    @Override
    public boolean setBlock(int x, int y, int z, int blockId, int meta) {
        return this.setBlockAtLayer(x, y, z, Block.LAYER_NORMAL, blockId, meta);
    }

    @Override
    public boolean setBlockAtLayer(int x, int y, int z, BlockLayer layer, int blockId) {
        return this.setBlockAtLayer(x, y, z, layer, blockId, 0);
    }

    @Override
    public boolean setBlockAtLayer(int x, int y, int z, BlockLayer layer, int blockId, int meta) {
        int fullId = (blockId << Block.DATA_BITS) | meta;
        synchronized (this.storages) {
            boolean success = this.computeStorage(layer).getAndSetFullBlock(x, y, z, fullId) != fullId;
            if (layer != Block.LAYER_WATERLOGGED && blockId == 0 && this.hasSecondLayer()) {
                this.computeStorage(Block.LAYER_WATERLOGGED).setFullBlock(x, y, z, 0);
            }
            return success;
        }
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
        // We don't support old byte format
        return new byte[0];
    }

    @Override
    public byte[] getDataArray() {
        // We don't support old byte format
        return new byte[0];
    }

    @Override
    public void writeTo(BinaryStream stream) {
        synchronized (this.storages) {
            boolean waterLogging = this.hasSecondLayer();
            stream.putByte((byte) 9); // SubChunk version
            stream.putByte((byte) (waterLogging ? 2 : 1)); // layers
            stream.putByte((byte) this.y);
            this.storages[0].writeTo(stream);
            if (waterLogging) {
                this.storages[1].writeTo(stream);
            }
        }
    }

    private boolean hasSecondLayer() {
        return this.storages.length > 1 && this.storages[1] != null;
    }

    @Override
    public byte[] getSkyLightArray() {
        if (this.skyLight != null) return this.skyLight;
        if (this.hasSkyLight) {
            if (this.compressedLight != null) {
                this.inflate();
                if (this.skyLight != null) return this.skyLight;
            }
        }
        return EmptyChunkSection.EMPTY_SKY_LIGHT_ARR;
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
                if (blockLight == null) {
                    blockLight = new byte[2048];
                }
                skyLight = new byte[2048];
                if (hasSkyLight) {
                    Arrays.fill(skyLight, (byte) 0xFF);
                }
            }
        } catch (IOException e) {
            Server.getInstance().getLogger().logException(e);
        }
    }

    @Override
    public byte[] getLightArray() {
        if (this.blockLight != null) return this.blockLight;
        if (this.hasBlockLight) {
            this.inflate();
            if (this.blockLight != null) return this.blockLight;
        }
        return EmptyChunkSection.EMPTY_LIGHT_ARR;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ChunkSection copy() {
        StateBlockStorage[] storages = new StateBlockStorage[this.storages.length];
        for (int i = 0; i < this.storages.length; i++) {
            if (this.storages[i] != null) {
                storages[i] = this.storages[i].copy();
            }
        }

        return new LevelDBChunkSection(
                this.y,
                storages,
                this.blockLight == null ? null : this.blockLight.clone(),
                this.skyLight == null ? null : this.skyLight.clone(),
                this.compressedLight == null ? null : this.compressedLight.clone(),
                this.hasBlockLight,
                this.hasSkyLight
        );
    }

    @Override
    public ChunkSection copyForChunkSending() {
        StateBlockStorage[] storages = new StateBlockStorage[this.storages.length];
        for (int i = 0; i < this.storages.length; i++) {
            if (this.storages[i] != null) {
                storages[i] = this.storages[i].copy();
            }
        }

        return new LevelDBChunkSection(
                this.y,
                storages,
                null,
                null,
                null,
                false,
                false
        );
    }

    public boolean compress() {
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
                    Server.getInstance().getLogger().logException(e);
                }
            }
            return true;
        }
        return false;
    }
}
