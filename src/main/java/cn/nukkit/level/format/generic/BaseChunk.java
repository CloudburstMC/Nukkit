package cn.nukkit.level.format.generic;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.level.format.Chunk;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.utils.ChunkException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * author: MagicDroidX
 * Nukkit Project
 */

public abstract class BaseChunk extends BaseFullChunk implements Chunk {

    protected ChunkSection[] sections;

    @Override
    public BaseChunk clone() {
        final BaseChunk chunk = (BaseChunk) super.clone();
        if (this.biomes != null) {
            chunk.biomes = this.biomes.clone();
        }
        chunk.heightMap = this.getHeightMapArray().clone();
        if (this.sections != null && this.sections[0] != null) {
            chunk.sections = new ChunkSection[this.sections.length];
            for (int i = 0; i < this.sections.length; i++) {
                chunk.sections[i] = this.sections[i].copy();
            }
        }
        return chunk;
    }

    @Override
    public LevelProvider getProvider() {
        return this.provider;
    }

    @Override
    public boolean load() throws IOException {
        return this.load(true);
    }

    @Override
    public boolean load(final boolean generate) throws IOException {
        return this.getProvider() != null && this.getProvider().getChunk(this.getX(), this.getZ(), true) != null;
    }

    @Override
    public byte[] getHeightMapArray() {
        return this.heightMap;
    }

    @Override
    public byte[] getBlockIdArray() {
        final ByteBuffer buffer = ByteBuffer.allocate(4096 * Chunk.SECTION_COUNT);
        for (int y = 0; y < Chunk.SECTION_COUNT; y++) {
            buffer.put(this.sections[y].getIdArray());
        }
        return buffer.array();
    }

    @Override
    public byte[] getBlockDataArray() {
        final ByteBuffer buffer = ByteBuffer.allocate(2048 * Chunk.SECTION_COUNT);
        for (int y = 0; y < Chunk.SECTION_COUNT; y++) {
            buffer.put(this.sections[y].getDataArray());
        }
        return buffer.array();
    }

    @Override
    public byte[] getBlockSkyLightArray() {
        final ByteBuffer buffer = ByteBuffer.allocate(2048 * Chunk.SECTION_COUNT);
        for (int y = 0; y < Chunk.SECTION_COUNT; y++) {
            buffer.put(this.sections[y].getSkyLightArray());
        }
        return buffer.array();
    }

    @Override
    public byte[] getBlockLightArray() {
        final ByteBuffer buffer = ByteBuffer.allocate(2048 * Chunk.SECTION_COUNT);
        for (int y = 0; y < Chunk.SECTION_COUNT; y++) {
            buffer.put(this.sections[y].getLightArray());
        }
        return buffer.array();
    }

    @Override
    public int getFullBlock(final int x, final int y, final int z) {
        return this.sections[y >> 4].getFullBlock(x, y & 0x0f, z);
    }

    @Override
    public Block getAndSetBlock(final int x, final int y, final int z, final Block block) {
        final int Y = y >> 4;
        try {
            this.setChanged();
            return this.sections[Y].getAndSetBlock(x, y & 0x0f, z, block);
        } catch (final ChunkException e) {
            try {
                this.setInternalSection(Y, (ChunkSection) this.providerClass.getMethod("createChunkSection", int.class).invoke(this.providerClass, Y));
            } catch (final IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
                Server.getInstance().getLogger().logException(e1);
            }
            return this.sections[Y].getAndSetBlock(x, y & 0x0f, z, block);
        } finally {
            this.removeInvalidTile(x, y, z);
        }
    }

    @Override
    public boolean setFullBlockId(final int x, final int y, final int z, final int fullId) {
        final int Y = y >> 4;
        try {
            this.setChanged();
            return this.sections[Y].setFullBlockId(x, y & 0x0f, z, fullId);
        } catch (final ChunkException e) {
            try {
                this.setInternalSection(Y, (ChunkSection) this.providerClass.getMethod("createChunkSection", int.class).invoke(this.providerClass, Y));
            } catch (final IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
                Server.getInstance().getLogger().logException(e1);
            }
            return this.sections[Y].setFullBlockId(x, y & 0x0f, z, fullId);
        } finally {
            this.removeInvalidTile(x, y, z);
        }
    }

    @Override
    public boolean setBlock(final int x, final int y, final int z, final int blockId) {
        return this.setBlock(x, y, z, blockId, 0);
    }

    @Override
    public boolean setBlock(final int x, final int y, final int z, final int blockId, final int meta) {
        final int Y = y >> 4;
        try {
            this.setChanged();
            return this.sections[Y].setBlock(x, y & 0x0f, z, blockId, meta);
        } catch (final ChunkException e) {
            try {
                this.setInternalSection(Y, (ChunkSection) this.providerClass.getMethod("createChunkSection", int.class).invoke(this.providerClass, Y));
            } catch (final IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
                Server.getInstance().getLogger().logException(e1);
            }
            return this.sections[Y].setBlock(x, y & 0x0f, z, blockId, meta);
        } finally {
            this.removeInvalidTile(x, y, z);
        }
    }

    @Override
    public int getBlockId(final int x, final int y, final int z) {
        return this.sections[y >> 4].getBlockId(x, y & 0x0f, z);
    }

    @Override
    public void setBlockId(final int x, final int y, final int z, final int id) {
        final int Y = y >> 4;
        try {
            this.sections[Y].setBlockId(x, y & 0x0f, z, id);
            this.setChanged();
        } catch (final ChunkException e) {
            try {
                this.setInternalSection(Y, (ChunkSection) this.providerClass.getMethod("createChunkSection", int.class).invoke(this.providerClass, Y));
            } catch (final IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
                Server.getInstance().getLogger().logException(e1);
            }
            this.sections[Y].setBlockId(x, y & 0x0f, z, id);
        } finally {
            this.removeInvalidTile(x, y, z);
        }
    }

    @Override
    public int getBlockData(final int x, final int y, final int z) {
        return this.sections[y >> 4].getBlockData(x, y & 0x0f, z);
    }

    @Override
    public void setBlockData(final int x, final int y, final int z, final int data) {
        final int Y = y >> 4;
        try {
            this.sections[Y].setBlockData(x, y & 0x0f, z, data);
            this.setChanged();
        } catch (final ChunkException e) {
            try {
                this.setInternalSection(Y, (ChunkSection) this.providerClass.getMethod("createChunkSection", int.class).invoke(this.providerClass, Y));
            } catch (final IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
                Server.getInstance().getLogger().logException(e1);
            }
            this.sections[Y].setBlockData(x, y & 0x0f, z, data);
        } finally {
            this.removeInvalidTile(x, y, z);
        }
    }

    @Override
    public int getBlockSkyLight(final int x, final int y, final int z) {
        return this.sections[y >> 4].getBlockSkyLight(x, y & 0x0f, z);
    }

    @Override
    public void setBlockSkyLight(final int x, final int y, final int z, final int level) {
        final int Y = y >> 4;
        try {
            this.sections[Y].setBlockSkyLight(x, y & 0x0f, z, level);
            this.setChanged();
        } catch (final ChunkException e) {
            try {
                this.setInternalSection(Y, (ChunkSection) this.providerClass.getMethod("createChunkSection", int.class).invoke(this.providerClass, Y));
            } catch (final IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
                Server.getInstance().getLogger().logException(e1);
            }
            this.sections[Y].setBlockSkyLight(x, y & 0x0f, z, level);
        }
    }

    @Override
    public int getBlockLight(final int x, final int y, final int z) {
        return this.sections[y >> 4].getBlockLight(x, y & 0x0f, z);
    }

    @Override
    public void setBlockLight(final int x, final int y, final int z, final int level) {
        final int Y = y >> 4;
        try {
            this.sections[Y].setBlockLight(x, y & 0x0f, z, level);
            this.setChanged();
        } catch (final ChunkException e) {
            try {
                this.setInternalSection(Y, (ChunkSection) this.providerClass.getMethod("createChunkSection", int.class).invoke(this.providerClass, Y));
            } catch (final IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
                Server.getInstance().getLogger().logException(e1);
            }
            this.sections[Y].setBlockLight(x, y & 0x0f, z, level);
        }
    }

    @Override
    public boolean isSectionEmpty(final float fY) {
        return this.sections[(int) fY] instanceof EmptyChunkSection;
    }

    @Override
    public ChunkSection getSection(final float fY) {
        return this.sections[(int) fY];
    }

    @Override
    public boolean setSection(final float fY, final ChunkSection section) {
        final byte[] emptyIdArray = new byte[4096];
        final byte[] emptyDataArray = new byte[2048];
        if (Arrays.equals(emptyIdArray, section.getIdArray()) && Arrays.equals(emptyDataArray, section.getDataArray())) {
            this.sections[(int) fY] = EmptyChunkSection.EMPTY[(int) fY];
        } else {
            this.sections[(int) fY] = section;
        }
        this.setChanged();
        return true;
    }

    @Override
    public ChunkSection[] getSections() {
        return this.sections;
    }

    private void removeInvalidTile(final int x, final int y, final int z) {
        final BlockEntity entity = this.getTile(x, y, z);
        if (entity != null && !entity.isBlockEntityValid()) {
            this.removeBlockEntity(entity);
        }
    }

    private void setInternalSection(final float fY, final ChunkSection section) {
        this.sections[(int) fY] = section;
        this.setChanged();
    }

}
