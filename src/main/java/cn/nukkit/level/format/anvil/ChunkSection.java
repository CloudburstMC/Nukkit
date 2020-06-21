package cn.nukkit.level.format.anvil;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.anvil.util.BlockStorage;
import cn.nukkit.level.format.anvil.util.NibbleArray;
import cn.nukkit.level.format.generic.EmptyChunkSection;
import cn.nukkit.level.util.PalettedBlockStorage;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.*;
import java.io.IOException;
import java.util.Arrays;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ChunkSection implements cn.nukkit.level.format.ChunkSection {

    private static final PalettedBlockStorage EMPTY_STORAGE = new PalettedBlockStorage();

    private final int y;

    private final BlockStorage storage;

    protected byte[] blockLight;

    protected byte[] skyLight;

    protected byte[] compressedLight;

    protected boolean hasBlockLight;

    protected boolean hasSkyLight;

    public ChunkSection(final int y, final BlockStorage storage, final byte[] blockLight, final byte[] skyLight, final byte[] compressedLight,
                        final boolean hasBlockLight, final boolean hasSkyLight) {
        this.y = y;
        this.storage = storage;
        this.blockLight = blockLight;
        this.skyLight = skyLight;
        this.compressedLight = compressedLight;
        this.hasBlockLight = hasBlockLight;
        this.hasSkyLight = hasSkyLight;
    }

    public ChunkSection(final int y) {
        this.y = y;

        this.hasBlockLight = false;
        this.hasSkyLight = false;

        this.storage = new BlockStorage();
    }

    public ChunkSection(final CompoundTag nbt) {
        this.y = nbt.getByte("Y");

        final byte[] blocks = nbt.getByteArray("Blocks");
        final NibbleArray data = new NibbleArray(nbt.getByteArray("Data"));

        this.storage = new BlockStorage();

        // Convert YZX to XZY
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 16; y++) {
                    final int index = ChunkSection.getAnvilIndex(x, y, z);
                    this.storage.setBlockId(x, y, z, blocks[index]);
                    this.storage.setBlockData(x, y, z, data.get(index));
                }
            }
        }

        this.blockLight = nbt.getByteArray("BlockLight");
        this.skyLight = nbt.getByteArray("SkyLight");
    }

    private static int getAnvilIndex(final int x, final int y, final int z) {
        return (y << 8) + (z << 4) + x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public int getBlockId(final int x, final int y, final int z) {
        synchronized (this.storage) {
            return this.storage.getBlockId(x, y, z);
        }
    }

    @Override
    public void setBlockId(final int x, final int y, final int z, final int id) {
        synchronized (this.storage) {
            this.storage.setBlockId(x, y, z, id);
        }
    }

    @Override
    public int getBlockData(final int x, final int y, final int z) {
        synchronized (this.storage) {
            return this.storage.getBlockData(x, y, z);
        }
    }

    @Override
    public void setBlockData(final int x, final int y, final int z, final int data) {
        synchronized (this.storage) {
            this.storage.setBlockData(x, y, z, data);
        }
    }

    @Override
    public int getFullBlock(final int x, final int y, final int z) {
        synchronized (this.storage) {
            return this.storage.getFullBlock(x, y, z);
        }
    }

    @Override
    public Block getAndSetBlock(final int x, final int y, final int z, final Block block) {
        synchronized (this.storage) {
            final int fullId = this.storage.getAndSetFullBlock(x, y, z, block.getFullId());
            return Block.fullList[fullId].clone();
        }
    }

    @Override
    public boolean setFullBlockId(final int x, final int y, final int z, final int fullId) {
        synchronized (this.storage) {
            this.storage.setFullBlock(x, y, z, (char) fullId);
        }
        return true;
    }

    @Override
    public boolean setBlock(final int x, final int y, final int z, final int blockId) {
        synchronized (this.storage) {
            return this.setBlock(x, y, z, blockId, 0);
        }
    }

    @Override
    public boolean setBlock(final int x, final int y, final int z, final int blockId, final int meta) {
        final int newFullId = (blockId << 4) + meta;
        synchronized (this.storage) {
            final int previousFullId = this.storage.getAndSetFullBlock(x, y, z, newFullId);
            return newFullId != previousFullId;
        }
    }

    @Override
    public int getBlockSkyLight(final int x, final int y, final int z) {
        if (this.skyLight == null) {
            if (!this.hasSkyLight) {
                return 0;
            } else if (this.compressedLight == null) {
                return 15;
            }
        }
        this.skyLight = this.getSkyLightArray();
        final int sl = this.skyLight[y << 7 | z << 3 | x >> 1] & 0xff;
        if ((x & 1) == 0) {
            return sl & 0x0f;
        }
        return sl >> 4;
    }

    @Override
    public void setBlockSkyLight(final int x, final int y, final int z, final int level) {
        if (this.skyLight == null) {
            if (this.hasSkyLight && this.compressedLight != null) {
                this.skyLight = this.getSkyLightArray();
            } else if (level == (this.hasSkyLight ? 15 : 0)) {
                return;
            } else {
                this.skyLight = new byte[2048];
                if (this.hasSkyLight) {
                    Arrays.fill(this.skyLight, (byte) 0xFF);
                }
            }
        }
        final int i = y << 7 | z << 3 | x >> 1;
        final int old = this.skyLight[i] & 0xff;
        if ((x & 1) == 0) {
            this.skyLight[i] = (byte) (old & 0xf0 | level & 0x0f);
        } else {
            this.skyLight[i] = (byte) ((level & 0x0f) << 4 | old & 0x0f);
        }
    }

    @Override
    public int getBlockLight(final int x, final int y, final int z) {
        if (this.blockLight == null && !this.hasBlockLight) {
            return 0;
        }
        this.blockLight = this.getLightArray();
        final int l = this.blockLight[y << 7 | z << 3 | x >> 1] & 0xff;
        if ((x & 1) == 0) {
            return l & 0x0f;
        }
        return l >> 4;
    }

    @Override
    public void setBlockLight(final int x, final int y, final int z, final int level) {
        if (this.blockLight == null) {
            if (this.hasBlockLight) {
                this.blockLight = this.getLightArray();
            } else if (level == 0) {
                return;
            } else {
                this.blockLight = new byte[2048];
            }
        }
        final int i = y << 7 | z << 3 | x >> 1;
        final int old = this.blockLight[i] & 0xff;
        if ((x & 1) == 0) {
            this.blockLight[i] = (byte) (old & 0xf0 | level & 0x0f);
        } else {
            this.blockLight[i] = (byte) ((level & 0x0f) << 4 | old & 0x0f);
        }
    }

    @Override
    public byte[] getIdArray() {
        synchronized (this.storage) {
            final byte[] anvil = new byte[4096];
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < 16; y++) {
                        final int index = ChunkSection.getAnvilIndex(x, y, z);
                        anvil[index] = (byte) this.storage.getBlockId(x, y, z);
                    }
                }
            }
            return anvil;
        }
    }

    @Override
    public byte[] getDataArray() {
        synchronized (this.storage) {
            final NibbleArray anvil = new NibbleArray(4096);
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < 16; y++) {
                        final int index = ChunkSection.getAnvilIndex(x, y, z);
                        anvil.set(index, (byte) this.storage.getBlockData(x, y, z));
                    }
                }
            }
            return anvil.getData();
        }
    }

    @Override
    public byte[] getSkyLightArray() {
        if (this.skyLight != null) {
            return this.skyLight;
        }
        if (this.hasSkyLight) {
            if (this.compressedLight != null) {
                this.inflate();
                return this.skyLight;
            }
            return EmptyChunkSection.EMPTY_SKY_LIGHT_ARR;
        } else {
            return EmptyChunkSection.EMPTY_LIGHT_ARR;
        }
    }

    @Override
    public byte[] getLightArray() {
        if (this.blockLight != null) {
            return this.blockLight;
        }
        if (this.hasBlockLight) {
            this.inflate();
            return this.blockLight;
        } else {
            return EmptyChunkSection.EMPTY_LIGHT_ARR;
        }
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void writeTo(final BinaryStream stream) {
        synchronized (this.storage) {
            stream.putByte((byte) 8); // Paletted chunk because Mojang messed up the old one
            stream.putByte((byte) 2);
            this.storage.writeTo(stream);
            ChunkSection.EMPTY_STORAGE.writeTo(stream);
        }
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

    public boolean compress() {
        if (this.blockLight != null) {
            final byte[] arr1 = this.blockLight;
            this.hasBlockLight = !Utils.isByteArrayEmpty(arr1);
            final byte[] arr2;
            if (this.skyLight != null) {
                arr2 = this.skyLight;
                this.hasSkyLight = !Utils.isByteArrayEmpty(arr2);
            } else if (this.hasSkyLight) {
                arr2 = EmptyChunkSection.EMPTY_SKY_LIGHT_ARR;
            } else {
                arr2 = EmptyChunkSection.EMPTY_LIGHT_ARR;
                this.hasSkyLight = false;
            }
            this.blockLight = null;
            this.skyLight = null;
            byte[] toDeflate = null;
            if (this.hasBlockLight && this.hasSkyLight && arr2 != EmptyChunkSection.EMPTY_SKY_LIGHT_ARR) {
                toDeflate = Binary.appendBytes(arr1, arr2);
            } else if (this.hasBlockLight) {
                toDeflate = arr1;
            }
            if (toDeflate != null) {
                try {
                    this.compressedLight = Zlib.deflate(toDeflate, 1);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
        return false;
    }

    private void inflate() {
        try {
            if (this.compressedLight != null && this.compressedLight.length != 0) {
                final byte[] inflated = Zlib.inflate(this.compressedLight);
                this.blockLight = Arrays.copyOfRange(inflated, 0, 2048);
                if (inflated.length > 2048) {
                    this.skyLight = Arrays.copyOfRange(inflated, 2048, 4096);
                } else {
                    this.skyLight = new byte[2048];
                    if (this.hasSkyLight) {
                        Arrays.fill(this.skyLight, (byte) 0xFF);
                    }
                }
                this.compressedLight = null;
            } else {
                this.blockLight = new byte[2048];
                this.skyLight = new byte[2048];
                if (this.hasSkyLight) {
                    Arrays.fill(this.skyLight, (byte) 0xFF);
                }
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] toXZY(final char[] raw) {
        final byte[] buffer = ThreadCache.byteCache6144.get();
        for (int i = 0; i < 4096; i++) {
            buffer[i] = (byte) (raw[i] >> 4);
        }
        for (int i = 0, j = 4096; i < 4096; i += 2, j++) {
            buffer[j] = (byte) ((raw[i + 1] & 0xF) << 4 | raw[i] & 0xF);
        }
        return buffer;
    }

    @Override
    public BlockStorage getStorage() {
        return storage;
    }
}
