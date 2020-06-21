package cn.nukkit.level.format.wool;

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
public class WoolChunkSection implements cn.nukkit.level.format.ChunkSection {

    private static final PalettedBlockStorage EMPTY_STORAGE = new PalettedBlockStorage();

    private final int y;

    protected final BlockStorage storage;

    protected byte[] blockLight;

    protected byte[] skyLight;

    public WoolChunkSection(int xy, BlockStorage storage, byte[] blockLight, byte[] skyLight) {
        this.y = xy;
        this.storage = storage;
        this.blockLight = blockLight;
        this.skyLight = skyLight;
    }

    public WoolChunkSection(final int y) {
        this.y = y;

        this.storage = new BlockStorage();
    }

    public WoolChunkSection(final int ind, final CompoundTag nbt) {
        this.y = ind;

        final byte[] blocks = nbt.getByteArray("blocks");
        final NibbleArray data = new NibbleArray(nbt.getByteArray("data"));

        this.storage = new BlockStorage(blocks,data);

        this.blockLight = nbt.getByteArray("blockLight");
        this.skyLight = nbt.getByteArray("skyLight");
    }

    private static int getIndex(final int x, final int y, final int z) {
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
            return 0;
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
            if (level == 0) {
                return;
            } else {
                this.skyLight = new byte[2048];
                Arrays.fill(this.skyLight, (byte) 0xFF);
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
        if (this.blockLight == null) {
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
            if (level == 0) {
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
                        final int index = WoolChunkSection.getIndex(x, y, z);
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
                        final int index = WoolChunkSection.getIndex(x, y, z);
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
        return EmptyChunkSection.EMPTY_LIGHT_ARR;
    }

    @Override
    public byte[] getLightArray() {
        if (this.blockLight != null) {
            return this.blockLight;
        }
        return EmptyChunkSection.EMPTY_LIGHT_ARR;
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
            WoolChunkSection.EMPTY_STORAGE.writeTo(stream);
        }
    }

    @Override
    public WoolChunkSection copy() {
        return new WoolChunkSection(
            this.y,
            this.storage,
            this.blockLight == null ? null : this.blockLight.clone(),
            this.skyLight == null ? null : this.skyLight.clone()
        );
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
