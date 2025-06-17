package cn.nukkit.level.format.anvil;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLayer;
import cn.nukkit.level.format.anvil.util.BlockStorage;
import cn.nukkit.level.format.anvil.util.NibbleArray;
import cn.nukkit.level.format.generic.EmptyChunkSection;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.Utils;
import cn.nukkit.utils.Zlib;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class ChunkSection implements cn.nukkit.level.format.ChunkSection {

    private final int y;

    private final BlockStorage storage;

    protected byte[] blockLight;
    protected byte[] skyLight;
    protected byte[] compressedLight;
    protected boolean hasBlockLight;
    protected boolean hasSkyLight;

    public ChunkSection(int y, BlockStorage storage, byte[] blockLight, byte[] skyLight, byte[] compressedLight, boolean hasBlockLight, boolean hasSkyLight) {
        this.y = y;
        this.storage = storage;
        this.blockLight = blockLight;
        this.skyLight = skyLight;
        this.compressedLight = compressedLight;
        this.hasBlockLight = hasBlockLight;
        this.hasSkyLight = hasSkyLight;
    }

    public ChunkSection(int y) {
        this(y, new BlockStorage(), null, null, null, false, true);
    }

    public ChunkSection(CompoundTag nbt) {
        this.y = nbt.getByte("Y");

        byte[] blocks = nbt.getByteArray("Blocks");
        NibbleArray data = new NibbleArray(nbt.getByteArray("Data"));

        storage = new BlockStorage();

        // Convert YZX to XZY
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 16; y++) {
                    int index = getAnvilIndex(x, y, z);
                    // Set block data first so we can overwrite it when removing data values from air in setBlockId
                    storage.setBlockData(x, y, z, data.get(index));
                    int b = blocks[index] & 0xff;
                    storage.setBlockId(x, y, z, b);
                }
            }
        }

        this.blockLight = nbt.getByteArray("BlockLight");
        this.skyLight = nbt.getByteArray("SkyLight");
    }

    private static int getAnvilIndex(int x, int y, int z) {
        return (y << 8) + (z << 4) + x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getBlockId(int x, int y, int z, BlockLayer layer) {
        if (layer != BlockLayer.NORMAL) {
            return 0;
        }
        synchronized (storage) {
            return storage.getBlockId(x, y, z);
        }
    }

    @Override
    public void setBlockId(int x, int y, int z, BlockLayer layer, int id) {
        if (layer != BlockLayer.NORMAL) {
            return;
        }

        synchronized (storage) {
            storage.setBlockId(x, y, z, id);
        }
    }

    @Override
    public boolean setFullBlockId(int x, int y, int z, BlockLayer layer, int fullId) {
        if (layer != BlockLayer.NORMAL) {
            return true;
        }

        synchronized (storage) {
            storage.setFullBlock(x, y, z, fullId);
        }
        return true;
    }

    @Override
    public int getBlockData(int x, int y, int z, BlockLayer layer) {
        if (layer != BlockLayer.NORMAL) {
            return 0;
        }

        synchronized (storage) {
            return storage.getBlockData(x, y, z);
        }
    }

    @Override
    public void setBlockData(int x, int y, int z, BlockLayer layer, int data) {
        if (layer != BlockLayer.NORMAL) {
            return;
        }

        synchronized (storage) {
            storage.setBlockData(x, y, z, data);
        }
    }

    @Override
    public int getFullBlock(int x, int y, int z, BlockLayer layer) {
        if (layer != BlockLayer.NORMAL) {
            return 0;
        }

        synchronized (storage) {
            return storage.getFullBlock(x, y, z);
        }
    }

    @Override
    public boolean setBlock(int x, int y, int z, int blockId) {
        synchronized (storage) {
            return setBlock(x, y, z, blockId, 0);
        }
    }

    public Block getAndSetBlock(int x, int y, int z, BlockLayer layer, Block block) {
        if (layer != BlockLayer.NORMAL) {
            return Block.get(Block.AIR);
        }

        synchronized (storage) {
            int fullId = storage.getAndSetFullBlock(x, y, z, block.getFullId());
            return Block.get(fullId, null, x, y, z, layer);
        }
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
        if (layer != BlockLayer.NORMAL) {
            return true;
        }

        int fullId = (blockId << Block.DATA_BITS) | meta;
        synchronized (storage) {
            return storage.getAndSetFullBlock(x, y, z, fullId) != fullId;
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
        synchronized (storage) {
            byte[] anvil = new byte[4096];
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < 16; y++) {
                        int index = getAnvilIndex(x, y, z);
                        anvil[index] = (byte) storage.getBlockId(x, y, z);
                    }
                }
            }
            return anvil;
        }
    }

    @Override
    public byte[] getDataArray() {
        synchronized (storage) {
            NibbleArray anvil = new NibbleArray(4096);
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < 16; y++) {
                        int index = getAnvilIndex(x, y, z);
                        anvil.set(index, (byte) storage.getBlockData(x, y, z));
                    }
                }
            }
            return anvil.getData();
        }
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
    public void writeTo(BinaryStream stream) {
        synchronized (storage) {
            this.storage.writeTo(this.y, stream);
        }
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

    @Override
    public ChunkSection copy() {
        return new ChunkSection(
                this.y,
                this.storage.copy(),
                this.blockLight == null ? null : this.blockLight.clone(),
                this.skyLight == null ? null : this.skyLight.clone(),
                this.compressedLight == null ? null : this.compressedLight.clone(),
                this.hasBlockLight,
                this.hasSkyLight
        );
    }

    @Override
    public ChunkSection copyForChunkSending() {
        return new ChunkSection(
                this.y,
                this.storage.copy(),
                null,
                null,
                null,
                false,
                false
        );
    }
}
