package cn.nukkit.level.format.generic;

import cn.nukkit.Server;
import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.Chunk;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.updater.ChunkUpdater;
import cn.nukkit.utils.ChunkException;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@Log4j2
public abstract class BaseChunk extends BaseFullChunk implements Chunk {
    @PowerNukkitOnly("Needed for level backward compatibility")
    @Since("1.3.0.0-PN")
    @Deprecated
    @DeprecationDetails(reason = "It's not a constant value and was moved to ChunkUpdater", replaceWith = "ChunkUpdater.getContentVersion()", 
            toBeRemovedAt = "1.5.0.0-PN", since = "1.4.0.0-PN")
    public static final int CONTENT_VERSION = ChunkUpdater.getContentVersion();

    protected ChunkSection[] sections;

    @PowerNukkitOnly("Needed for level backward compatibility")
    @Since("1.3.0.0-PN")
    @Override
    public void backwardCompatibilityUpdate(Level level) {
        ChunkUpdater.backwardCompatibilityUpdate(level, this);
    }

    @Override
    public BaseChunk clone() {
        BaseChunk chunk = (BaseChunk) super.clone();
        if (this.biomes != null) chunk.biomes = this.biomes.clone();
        chunk.heightMap = this.getHeightMapArray().clone();
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
            removeBlockEntity(entity);
        }
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    public int getFullBlock(int x, int y, int z) {
        return getFullBlock(x, y, z, 0);
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public int getFullBlock(int x, int y, int z, int layer) {
        return this.sections[y >> 4].getFullBlock(x, y & 0x0f, z, layer);
    }

    @PowerNukkitOnly
    @Override
    public BlockState getBlockState(int x, int y, int z, int layer) {
        return this.sections[y >> 4].getBlockState(x, y & 0x0f, z, layer);
    }

    @PowerNukkitOnly
    @Override
    public boolean setBlockAtLayer(int x, int y, int z, int layer, int blockId) {
        return this.setBlockStateAtLayer(x, y, z, layer, BlockState.of(blockId));
    }

    @Override
    public boolean setBlock(int x, int y, int z, int blockId) {
        return setBlockState(x, y, z, BlockState.of(blockId));
    }

    @Override
    public Block getAndSetBlock(int x, int y, int z, Block block) {
        return getAndSetBlock(x, y, z, 0, block);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    public BlockState getAndSetBlockState(int x, int y, int z, int layer, BlockState state) {
        int sectionY = y >> 4;
        try {
            setChanged();
            return this.sections[sectionY].getAndSetBlockState(x, y & 0x0f, z, layer, state);
        } catch (ChunkException e) {
            try {
                this.setInternalSection(sectionY, (ChunkSection) this.providerClass.getMethod("createChunkSection", int.class).invoke(this.providerClass, sectionY));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
                log.error("Error creating the chunk section");
                throw new ChunkException(e1);
            }
            return this.sections[sectionY].getAndSetBlockState(x, y & 0x0f, z, layer, state);
        } finally {
            removeInvalidTile(x, y, z);
        }
    }

    @PowerNukkitOnly
    @Override
    public Block getAndSetBlock(int x, int y, int z, int layer, Block block) {
        return getAndSetBlockState(x, y, z, layer, block.getCurrentState()).getBlock();
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN")
    @Override
    public boolean setFullBlockId(int x, int y, int z, int fullId) {
        return this.setFullBlockId(x, y, z, 0, fullId);
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN")
    @Override
    @PowerNukkitOnly
    public boolean setFullBlockId(int x, int y, int z, int layer, int fullId) {
        int Y = y >> 4;
        try {
            setChanged();
            return this.sections[Y].setFullBlockId(x, y & 0x0f, z, layer, fullId);
        } catch (ChunkException e) {
            try {
                this.setInternalSection(Y, (ChunkSection) this.providerClass.getMethod("createChunkSection", int.class).invoke(this.providerClass, Y));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
                Server.getInstance().getLogger().logException(e1);
            }
            return this.sections[Y].setFullBlockId(x, y & 0x0f, z, layer, fullId);
        } finally {
            removeInvalidTile(x, y, z);
        }
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean setBlock(int x, int y, int z, int blockId, int meta) {
        return this.setBlockAtLayer(x, y, z, 0, blockId, meta);
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean setBlockAtLayer(int x, int y, int z, int layer, int blockId, int meta) {
        return setBlockStateAtLayer(x, y, z, layer, BlockState.of(blockId, meta));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    public boolean setBlockStateAtLayer(int x, int y, int z, int layer, BlockState state) {
        int sectionY = y >> 4;
        try {
            setChanged();
            return this.sections[sectionY].setBlockStateAtLayer(x, y & 0x0f, z, layer, state);
        } catch (ChunkException e) {
            try {
                this.setInternalSection(sectionY, (ChunkSection) this.providerClass.getMethod("createChunkSection", int.class).invoke(this.providerClass, sectionY));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
                Server.getInstance().getLogger().logException(e1);
            }
            return this.sections[sectionY].setBlockStateAtLayer(x, y & 0x0f, z, layer, state);
        } finally {
            removeInvalidTile(x, y, z);
        }
    }

    @Override
    public void setBlockId(int x, int y, int z, int id) {
        setBlockStateAtLayer(x, y, z, 0, BlockState.of(id));
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    @PowerNukkitOnly
    public void setBlockId(int x, int y, int z, int layer, int id) {
        int sectionY = y >> 4;
        try {
            this.sections[sectionY].setBlockId(x, y & 0x0f, z, layer, id);
            setChanged();
        } catch (ChunkException e) {
            try {
                this.setInternalSection(sectionY, (ChunkSection) this.providerClass.getMethod("createChunkSection", int.class).invoke(this.providerClass, sectionY));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
                Server.getInstance().getLogger().logException(e1);
            }
            this.sections[sectionY].setBlockId(x, y & 0x0f, z, layer, id);
        } finally {
            removeInvalidTile(x, y, z);
        }
    }

    @Override
    public int getBlockId(int x, int y, int z) {
        return getBlockId(x, y, z, 0);
    }

    @Override
    @PowerNukkitOnly
    public int getBlockId(int x, int y, int z, int layer) {
        return this.sections[y >> 4].getBlockId(x, y & 0x0f, z, layer);
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    public int getBlockData(int x, int y, int z) {
        return getBlockData(x, y, z, 0);
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    public int getBlockData(int x, int y, int z, int layer) {
        return this.sections[y >> 4].getBlockData(x, y & 0x0f, z, layer);
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    public void setBlockData(int x, int y, int z, int data) {
        setBlockData(x, y, z, 0, data);
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    public void setBlockData(int x, int y, int z, int layer, int data) {
        int sectionY = y >> 4;
        try {
            this.sections[sectionY].setBlockData(x, y & 0x0f, z, layer, data);
            setChanged();
        } catch (ChunkException e) {
            try {
                this.setInternalSection(sectionY, (ChunkSection) this.providerClass.getMethod("createChunkSection", int.class).invoke(this.providerClass, sectionY));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
                Server.getInstance().getLogger().logException(e1);
            }
            this.sections[sectionY].setBlockData(x, y & 0x0f, z, layer, data);
        } finally {
            removeInvalidTile(x, y, z);
        }
    }

    @Override
    public int getBlockSkyLight(int x, int y, int z) {
        return this.sections[y >> 4].getBlockSkyLight(x, y & 0x0f, z);
    }

    @Override
    public void setBlockSkyLight(int x, int y, int z, int level) {
        int sectionY = y >> 4;
        try {
            this.sections[sectionY].setBlockSkyLight(x, y & 0x0f, z, level);
            setChanged();
        } catch (ChunkException e) {
            try {
                this.setInternalSection(sectionY, (ChunkSection) this.providerClass.getMethod("createChunkSection", int.class).invoke(this.providerClass, sectionY));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
                Server.getInstance().getLogger().logException(e1);
            }
            this.sections[sectionY].setBlockSkyLight(x, y & 0x0f, z, level);
        }
    }

    @Override
    public int getBlockLight(int x, int y, int z) {
        return this.sections[y >> 4].getBlockLight(x, y & 0x0f, z);
    }

    @Override
    public void setBlockLight(int x, int y, int z, int level) {
        int sectionY = y >> 4;
        try {
            this.sections[sectionY].setBlockLight(x, y & 0x0f, z, level);
            setChanged();
        } catch (ChunkException e) {
            try {
                this.setInternalSection(sectionY, (ChunkSection) this.providerClass.getMethod("createChunkSection", int.class).invoke(this.providerClass, sectionY));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
                Server.getInstance().getLogger().logException(e1);
            }
            this.sections[sectionY].setBlockLight(x, y & 0x0f, z, level);
        }
    }

    @Override
    public boolean isSectionEmpty(float fY) {
        return this.sections[(int) fY].isEmpty();
    }

    @Override
    public ChunkSection getSection(float fY) {
        return this.sections[(int) fY];
    }

    @Override
    public boolean setSection(float fY, ChunkSection section) {
        if (!section.hasBlocks()) {
            this.sections[(int) fY] = EmptyChunkSection.EMPTY[(int) fY];
        } else {
            this.sections[(int) fY] = section;
        }
        setChanged();
        return true;
    }

    private void setInternalSection(float fY, ChunkSection section) {
        this.sections[(int) fY] = section;
        setChanged();
    }

    @Override
    public boolean load() throws IOException {
        return this.load(true);
    }

    @Override
    public boolean load(boolean generate) throws IOException {
        return this.getProvider() != null && this.getProvider().getChunk(this.getX(), this.getZ(), true) != null;
    }

    @Override
    public byte[] getBlockSkyLightArray() {
        ByteBuffer buffer = ByteBuffer.allocate(2048 * SECTION_COUNT);
        for (int y = 0; y < SECTION_COUNT; y++) {
            buffer.put(this.sections[y].getSkyLightArray());
        }
        return buffer.array();
    }

    @Override
    public byte[] getBlockLightArray() {
        ByteBuffer buffer = ByteBuffer.allocate(2048 * SECTION_COUNT);
        for (int y = 0; y < SECTION_COUNT; y++) {
            buffer.put(this.sections[y].getLightArray());
        }
        return buffer.array();
    }

    @Override
    public ChunkSection[] getSections() {
        return sections;
    }

    @Override
    public byte[] getHeightMapArray() {
        return this.heightMap;
    }

    @Override
    public LevelProvider getProvider() {
        return this.provider;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    public boolean setBlockStateAt(int x, int y, int z, int layer, BlockState state) {
        return setBlockStateAtLayer(x, y, z, layer, state);
    }

    @Override
    public BlockState getBlockStateAt(int x, int y, int z, int layer) {
        return getBlockState(x, y, z, layer);
    }

}
