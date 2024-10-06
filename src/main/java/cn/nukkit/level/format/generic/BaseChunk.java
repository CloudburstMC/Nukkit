package cn.nukkit.level.format.generic;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLayer;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.level.format.Chunk;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.utils.ChunkException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @author MagicDroidX
 * Nukkit Project
 */

public abstract class BaseChunk extends BaseFullChunk implements Chunk {

    protected ChunkSection[] sections;

    @SuppressWarnings("MismatchedReadAndWriteOfArray")
    private static final byte[] emptyIdArray = new byte[4096];
    @SuppressWarnings("MismatchedReadAndWriteOfArray")
    private static final byte[] emptyDataArray = new byte[2048];

    @Override
    public BaseChunk clone() {
        BaseChunk chunk = (BaseChunk) super.clone();
        if (sections != null && sections[0] != null) {
            chunk.sections = new ChunkSection[sections.length];
            for (int i = 0; i < sections.length; i++) {
                chunk.sections[i] = sections[i].copy();
            }
        }
        return chunk;
    }

    @Override
    public BaseChunk cloneForChunkSending() {
        BaseChunk chunk = (BaseChunk) super.cloneForChunkSending();
        if (sections != null && sections[0] != null) {
            chunk.sections = new ChunkSection[sections.length];
            for (int i = 0; i < sections.length; i++) {
                chunk.sections[i] = sections[i].copy();
            }
        }
        return chunk;
    }

    private void removeInvalidTile(int x, int y, int z) {
        BlockEntity entity = getTile(x, y, z);
        if (entity != null && !entity.isBlockEntityValid()) {
            this.removeBlockEntity(entity);
            if (!entity.closed) {
                entity.closed = true;
                if (entity.level != null) {
                    entity.level.removeBlockEntity(entity);
                    entity.level = null;
                }
            }
        }
    }

    @Override
    public int getFullBlock(int x, int y, int z, BlockLayer layer) {
        return this.getSection(y >> 4).getFullBlock(x, y & 0x0f, z, layer);
    }

    @Override
    public boolean setBlock(int x, int y, int z, int blockId) {
        return this.setBlock(x, y, z, blockId, 0);
    }

    @Override
    public Block getAndSetBlock(int x, int y, int z, BlockLayer layer, Block block) {
        int Y = y >> 4;
        try {
            setChanged();
            return this.getSection(Y).getAndSetBlock(x, y & 0x0f, z, layer, block);
        } catch (ChunkException e) {
            try {
                this.setInternalSection(Y, (ChunkSection) this.providerClass.getMethod("createChunkSection", int.class).invoke(this.providerClass, Y));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
                Server.getInstance().getLogger().logException(e1);
            }
            return this.getSection(Y).getAndSetBlock(x, y & 0x0f, z, layer, block);
        } finally {
            removeInvalidTile(x, y, z);
        }
    }

    @Override
    public boolean setFullBlockId(int x, int y, int z, BlockLayer layer, int fullId) {
        int Y = y >> 4;
        try {
            setChanged();
            return this.getSection(Y).setFullBlockId(x, y & 0x0f, z, layer, fullId);
        } catch (ChunkException e) {
            try {
                this.setInternalSection(Y, (ChunkSection) this.providerClass.getMethod("createChunkSection", int.class).invoke(this.providerClass, Y));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
                Server.getInstance().getLogger().logException(e1);
            }
            return this.getSection(Y).setFullBlockId(x, y & 0x0f, z, layer, fullId);
        } finally {
            removeInvalidTile(x, y, z);
        }
    }

    @Override
    public boolean setBlock(int x, int y, int z, int blockId, int meta) {
        return this.setBlockAtLayer(x, y, z, BlockLayer.NORMAL, blockId, meta);
    }

    @Override
    public boolean setBlockAtLayer(int x, int y, int z, BlockLayer layer, int id) {
        return this.setBlockAtLayer(x, y, z, layer, id, 0);
    }

    @Override
    public boolean setBlockAtLayer(int x, int y, int z, BlockLayer layer, int blockId, int meta) {
        int Y = y >> 4;
        try {
            setChanged();
            return this.getSection(Y).setBlockAtLayer(x, y & 0x0f, z, layer, blockId, meta);
        } catch (ChunkException e) {
            try {
                this.setInternalSection(Y, (ChunkSection) this.providerClass.getMethod("createChunkSection", int.class).invoke(this.providerClass, Y));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
                Server.getInstance().getLogger().logException(e1);
            }
            return this.getSection(Y).setBlockAtLayer(x, y & 0x0f, z, layer, blockId, meta);
        } finally {
            removeInvalidTile(x, y, z);
        }
    }

    @Override
    public void setBlockId(int x, int y, int z, BlockLayer layer, int id) {
        int Y = y >> 4;
        try {
            this.getSection(Y).setBlockId(x, y & 0x0f, z, layer, id);
            setChanged();
        } catch (ChunkException e) {
            try {
                this.setInternalSection(Y, (ChunkSection) this.providerClass.getMethod("createChunkSection", int.class).invoke(this.providerClass, Y));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
                Server.getInstance().getLogger().logException(e1);
            }
            this.getSection(Y).setBlockId(x, y & 0x0f, z, layer, id);
        } finally {
            removeInvalidTile(x, y, z);
        }
    }

    public int getBlockId(int x, int y, int z, BlockLayer layer) {
        return this.getSection(y >> 4).getBlockId(x, y & 0x0f, z, layer);
    }

    @Override
    public int getBlockData(int x, int y, int z, BlockLayer layer) {
        return this.getSection(y >> 4).getBlockData(x, y & 0x0f, z, layer);
    }

    @Override
    public void setBlockData(int x, int y, int z, BlockLayer layer, int data) {
        int Y = y >> 4;
        try {
            this.getSection(Y).setBlockData(x, y & 0x0f, z, layer, data);
            setChanged();
        } catch (ChunkException e) {
            try {
                this.setInternalSection(Y, (ChunkSection) this.providerClass.getMethod("createChunkSection", int.class).invoke(this.providerClass, Y));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
                Server.getInstance().getLogger().logException(e1);
            }
            this.getSection(Y).setBlockData(x, y & 0x0f, z, layer, data);
        } finally {
            removeInvalidTile(x, y, z);
        }
    }

    @Override
    public int getBlockSkyLight(int x, int y, int z) {
        return this.getSection(y >> 4).getBlockSkyLight(x, y & 0x0f, z);
    }

    @Override
    public void setBlockSkyLight(int x, int y, int z, int level) {
        int Y = y >> 4;
        try {
            this.getSection(Y).setBlockSkyLight(x, y & 0x0f, z, level);
            setChanged();
        } catch (ChunkException e) {
            try {
                this.setInternalSection(Y, (ChunkSection) this.providerClass.getMethod("createChunkSection", int.class).invoke(this.providerClass, Y));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
                Server.getInstance().getLogger().logException(e1);
            }
            this.getSection(Y).setBlockSkyLight(x, y & 0x0f, z, level);
        }
    }

    @Override
    public int getBlockLight(int x, int y, int z) {
        return this.getSection(y >> 4).getBlockLight(x, y & 0x0f, z);
    }

    @Override
    public void setBlockLight(int x, int y, int z, int level) {
        int Y = y >> 4;
        try {
            this.getSection(Y).setBlockLight(x, y & 0x0f, z, level);
            setChanged();
        } catch (ChunkException e) {
            try {
                this.setInternalSection(Y, (ChunkSection) this.providerClass.getMethod("createChunkSection", int.class).invoke(this.providerClass, Y));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
                Server.getInstance().getLogger().logException(e1);
            }
            this.getSection(Y).setBlockLight(x, y & 0x0f, z, level);
        }
    }

    @Override
    public boolean isSectionEmpty(float fY) {
        return this.getSection(fY) instanceof EmptyChunkSection;
    }

    @Override
    public ChunkSection getSection(float fY) {
        int index = (this.getSectionOffset() + (int) fY);
        if (index >= this.sections.length) {
            Throwable t = new Throwable("Tried to get chunk section " + index + ", but chunk has only " + this.sections.length + " sections!");
            this.getProvider().getLevel().getServer().getLogger().logException(t);
            index = this.sections.length - 1;
        }
        return this.sections[index];
    }

    @Override
    public boolean setSection(float fY, ChunkSection section) {
        if (Arrays.equals(emptyIdArray, section.getIdArray()) && Arrays.equals(emptyDataArray, section.getDataArray())) {
            this.sections[this.getSectionOffset() + (int) fY] = EmptyChunkSection.EMPTY[(int) fY];
        } else {
            this.sections[this.getSectionOffset() + (int) fY] = section;
        }
        setChanged();
        return true;
    }

    private void setInternalSection(float fY, ChunkSection section) {
        this.sections[this.getSectionOffset() + (int) fY] = section;
        setChanged();
    }

    @Override
    public boolean load() throws IOException {
        return this.load(true);
    }

    @Override
    public boolean load(boolean generate) throws IOException {
        return this.provider != null && this.provider.getChunk(this.getX(), this.getZ(), true) != null;
    }

    @Override
    public byte[] getBlockIdArray() {
        ByteBuffer buffer = ByteBuffer.allocate(65536);
        for (int y = 0; y < SECTION_COUNT; y++) {
            buffer.put(this.getSection(y).getIdArray());
        }
        return buffer.array();
    }

    @Override
    public byte[] getBlockDataArray() {
        ByteBuffer buffer = ByteBuffer.allocate(32768);
        for (int y = 0; y < SECTION_COUNT; y++) {
            buffer.put(this.getSection(y).getDataArray());
        }
        return buffer.array();
    }

    @Override
    public byte[] getBlockSkyLightArray() {
        ByteBuffer buffer = ByteBuffer.allocate(32768);
        for (int y = 0; y < SECTION_COUNT; y++) {
            buffer.put(this.getSection(y).getSkyLightArray());
        }
        return buffer.array();
    }

    @Override
    public byte[] getBlockLightArray() {
        ByteBuffer buffer = ByteBuffer.allocate(32768);
        for (int y = 0; y < SECTION_COUNT; y++) {
            buffer.put(this.getSection(y).getLightArray());
        }
        return buffer.array();
    }

    @Override
    public ChunkSection[] getSections() {
        return sections;
    }
}
