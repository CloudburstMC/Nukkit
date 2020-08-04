package cn.nukkit.level.format.generic;

import cn.nukkit.Server;
import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockUnknown;
import cn.nukkit.block.BlockWall;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.Chunk;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.ChunkException;
import cn.nukkit.utils.Faceable;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * author: MagicDroidX
 * Nukkit Project
 */

public abstract class BaseChunk extends BaseFullChunk implements Chunk {
    @PowerNukkitOnly("Needed for level backward compatibility")
    @Since("1.3.0.0-PN")
    public static final int CONTENT_VERSION = 2;

    protected ChunkSection[] sections;

    @PowerNukkitOnly("Needed for level backward compatibility")
    @Since("1.3.0.0-PN")
    @Override
    public void backwardCompatibilityUpdate(Level level) {
        super.backwardCompatibilityUpdate(level);
        
        boolean updated = false;
        for (ChunkSection section : sections) {
            int contentVersion = section.getContentVersion();
            if (contentVersion < 2) {
                WallUpdater wallUpdater = new WallUpdater(level, section);
                boolean sectionUpdated = walk(section, new GroupedUpdaters(
                        wallUpdater,
                        contentVersion < 1? new StemUpdater(level, section, BlockID.MELON_STEM, BlockID.MELON_BLOCK) : null,
                        contentVersion < 1? new StemUpdater(level, section, BlockID.PUMPKIN_STEM, BlockID.PUMPKIN) : null
                ));

                updated = updated || sectionUpdated;
                
                int attempts = 0;
                while (sectionUpdated) {
                    if (attempts++ >= 5) {
                        int x = getX() << 4 | 0x6;
                        int y = section.getY() << 4 | 0x6;
                        int z = getZ() << 4 | 0x6;
                        Server.getInstance().getLogger().warning("The chunk section at x:"+x+", y:"+y+", z:"+z+" failed to complete the backward compatibility update 1 after "+attempts+" attempts");
                        break;
                    }
                    sectionUpdated = walk(section, wallUpdater);
                }
                
                section.setContentVersion(2);
            }
        }
        
        if (updated) {
            setChanged();
        }
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

    @Override
    public int getFullBlock(int x, int y, int z) {
        return getFullBlock(x, y, z, 0);
    }

    @Override
    public int getFullBlock(int x, int y, int z, int layer) {
        return this.sections[y >> 4].getFullBlock(x, y & 0x0f, z, layer);
    }

    @Override
    public int[] getBlockState(int x, int y, int z, int layer) {
        return this.sections[y >> 4].getBlockState(x, y & 0x0f, z, layer);
    }

    @Override
    public boolean setBlockAtLayer(int x, int y, int z, int layer, int blockId) {
        return this.setBlockAtLayer(x, y, z, layer, blockId, 0);
    }

    @Override
    public boolean setBlock(int x, int y, int z, int blockId) {
        return this.setBlock(x, y, z, blockId, 0);
    }

    @Override
    public Block getAndSetBlock(int x, int y, int z, Block block) {
        return getAndSetBlock(x, y, z, 0, block);
    }

    @Override
    public Block getAndSetBlock(int x, int y, int z, int layer, Block block) {
        int Y = y >> 4;
        try {
            setChanged();
            return this.sections[Y].getAndSetBlock(x, y & 0x0f, z, layer, block);
        } catch (ChunkException e) {
            try {
                this.setInternalSection(Y, (ChunkSection) this.providerClass.getMethod("createChunkSection", int.class).invoke(this.providerClass, Y));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
                Server.getInstance().getLogger().logException(e1);
            }
            return this.sections[Y].getAndSetBlock(x, y & 0x0f, z, layer, block);
        } finally {
            removeInvalidTile(x, y, z);
        }
    }

    @Deprecated
    @DeprecationDetails(reason = "Does not support hyper ids", since = "1.3.0.0-PN")
    @Override
    public boolean setFullBlockId(int x, int y, int z, int fullId) {
        return this.setFullBlockId(x, y, z, 0, fullId);
    }

    @Deprecated
    @DeprecationDetails(reason = "Does not support hyper ids", since = "1.3.0.0-PN")
    @Override
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

    @Override
    public boolean setBlock(int x, int y, int z, int blockId, int meta) {
        return this.setBlockAtLayer(x, y, z, 0, blockId, meta);
    }

    @Override
    public boolean setBlockAtLayer(int x, int y, int z, int layer, int blockId, int meta) {
        int Y = y >> 4;
        try {
            setChanged();
            return this.sections[Y].setBlockAtLayer(x, y & 0x0f, z, layer, blockId, meta);
        } catch (ChunkException e) {
            try {
                this.setInternalSection(Y, (ChunkSection) this.providerClass.getMethod("createChunkSection", int.class).invoke(this.providerClass, Y));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
                Server.getInstance().getLogger().logException(e1);
            }
            return this.sections[Y].setBlockAtLayer(x, y & 0x0f, z, layer, blockId, meta);
        } finally {
            removeInvalidTile(x, y, z);
        }
    }

    @Override
    public void setBlockId(int x, int y, int z, int id) {
        setBlockId(x, y, z, 0, id);
    }

    @Override
    public void setBlockId(int x, int y, int z, int layer, int id) {
        int Y = y >> 4;
        try {
            this.sections[Y].setBlockId(x, y & 0x0f, z, layer, id);
            setChanged();
        } catch (ChunkException e) {
            try {
                this.setInternalSection(Y, (ChunkSection) this.providerClass.getMethod("createChunkSection", int.class).invoke(this.providerClass, Y));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
                Server.getInstance().getLogger().logException(e1);
            }
            this.sections[Y].setBlockId(x, y & 0x0f, z, layer, id);
        } finally {
            removeInvalidTile(x, y, z);
        }
    }

    @Override
    public int getBlockId(int x, int y, int z) {
        return getBlockId(x, y, z, 0);
    }

    @Override
    public int getBlockId(int x, int y, int z, int layer) {
        return this.sections[y >> 4].getBlockId(x, y & 0x0f, z, layer);
    }

    @Override
    public int getBlockData(int x, int y, int z) {
        return getBlockData(x, y, z, 0);
    }

    @Override
    public int getBlockData(int x, int y, int z, int layer) {
        return this.sections[y >> 4].getBlockData(x, y & 0x0f, z, layer);
    }

    @Override
    public void setBlockData(int x, int y, int z, int data) {
        setBlockData(x, y, z, 0, data);
    }

    @Override
    public void setBlockData(int x, int y, int z, int layer, int data) {
        int Y = y >> 4;
        try {
            this.sections[Y].setBlockData(x, y & 0x0f, z, layer, data);
            setChanged();
        } catch (ChunkException e) {
            try {
                this.setInternalSection(Y, (ChunkSection) this.providerClass.getMethod("createChunkSection", int.class).invoke(this.providerClass, Y));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
                Server.getInstance().getLogger().logException(e1);
            }
            this.sections[Y].setBlockData(x, y & 0x0f, z, layer, data);
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
        int Y = y >> 4;
        try {
            this.sections[Y].setBlockSkyLight(x, y & 0x0f, z, level);
            setChanged();
        } catch (ChunkException e) {
            try {
                this.setInternalSection(Y, (ChunkSection) this.providerClass.getMethod("createChunkSection", int.class).invoke(this.providerClass, Y));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
                Server.getInstance().getLogger().logException(e1);
            }
            this.sections[Y].setBlockSkyLight(x, y & 0x0f, z, level);
        }
    }

    @Override
    public int getBlockLight(int x, int y, int z) {
        return this.sections[y >> 4].getBlockLight(x, y & 0x0f, z);
    }

    @Override
    public void setBlockLight(int x, int y, int z, int level) {
        int Y = y >> 4;
        try {
            this.sections[Y].setBlockLight(x, y & 0x0f, z, level);
            setChanged();
        } catch (ChunkException e) {
            try {
                this.setInternalSection(Y, (ChunkSection) this.providerClass.getMethod("createChunkSection", int.class).invoke(this.providerClass, Y));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
                Server.getInstance().getLogger().logException(e1);
            }
            this.sections[Y].setBlockLight(x, y & 0x0f, z, level);
        }
    }

    @Override
    public boolean isSectionEmpty(float fY) {
        return this.sections[(int) fY] instanceof EmptyChunkSection;
    }

    @Override
    public ChunkSection getSection(float fY) {
        return this.sections[(int) fY];
    }

    @Override
    public boolean setSection(float fY, ChunkSection section) {
        byte[] emptyIdArray = new byte[4096];
        byte[] emptyDataArray = new byte[2048];
        if (Arrays.equals(emptyIdArray, section.getIdArray()) && Arrays.equals(emptyDataArray, section.getDataArray())) {
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
    public byte[] getBlockIdArray(int layer) {
        ByteBuffer buffer = ByteBuffer.allocate(4096 * SECTION_COUNT);
        for (int y = 0; y < SECTION_COUNT; y++) {
            buffer.put(this.sections[y].getIdArray(layer));
        }
        return buffer.array();
    }

    @Override
    public byte[] getBlockDataArray(int layer) {
        ByteBuffer buffer = ByteBuffer.allocate(2048 * SECTION_COUNT);
        for (int y = 0; y < SECTION_COUNT; y++) {
            buffer.put(this.sections[y].getDataArray(layer));
        }
        return buffer.array();
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

    private boolean walk(ChunkSection section, Updater updater) {
        int offsetX = getX() << 4;
        int offsetZ = getZ() << 4;
        int offsetY = section.getY() << 4;
        boolean updated = false;
        for (int x = 0; x <= 0xF; x++) {
            for (int z = 0; z <= 0xF; z++) {
                for (int y = 0; y <= 0xF; y++) {
                    int[] state = section.getBlockState(x, y, z, 0);
                    updated |= updater.update(offsetX, offsetY, offsetZ, x, y, z, state[0], state[1]);
                }
            }
        }
        return updated;
    }


    @FunctionalInterface
    private interface Updater {
        boolean update(int offsetX, int offsetY, int offsetZ, int x, int y, int z, int blockId, int meta);
    }

    @RequiredArgsConstructor
    private static class WallUpdater implements Updater {
        private final Level level;
        private final ChunkSection section;

        @Override
        public boolean update(int offsetX, int offsetY, int offsetZ, int x, int y, int z, int blockId, int meta) {
            if (blockId != BlockID.COBBLE_WALL) {
                return false;
            }

            int levelX = offsetX + x;
            int levelY = offsetY + y;
            int levelZ = offsetZ + z;
            Block block = Block.get(blockId, meta, level, levelX, levelY, levelZ, 0);
            if (block instanceof BlockUnknown) {
                // Block was on an invalid state, clearing the state but keeping the material type
                block = Block.get(blockId, meta & 0xF, level, levelX, levelY, levelZ, 0);
                if (block instanceof BlockUnknown) {
                    Server.getInstance().getLogger()
                            .warning("Failed to update the block X:"+levelX+", Y:"+levelY+", Z:"+levelZ+" at "+level
                                    +", could not cast it to BlockWall.");
                    return false;
                }
            }
            
            BlockWall blockWall = (BlockWall) block;
            if (blockWall.autoConfigureState()) {
                section.setBlockData(x, y, z, 0, blockWall.getDamage());
                return true;
            }

            return false;
        }
    }

    @RequiredArgsConstructor
    private static class StemUpdater implements Updater {
        private final Level level;
        private final ChunkSection section;
        private final int stemId;
        private final int productId;

        @Override
        public boolean update(int offsetX, int offsetY, int offsetZ, int x, int y, int z, int blockId, int meta) {
            if (blockId != stemId) {
                return false;
            }

            for (BlockFace blockFace : BlockFace.Plane.HORIZONTAL) {
                int sideId = level.getBlockIdAt(
                        offsetX + x + blockFace.getXOffset(),
                        offsetY + y,
                        offsetZ + z + blockFace.getZOffset()
                );
                if (sideId == productId) {
                    Block blockStem = Block.get(blockId, meta, level, offsetX + x, offsetY + y, offsetZ + z, 0);
                    ((Faceable) blockStem).setBlockFace(blockFace);
                    section.setBlockData(x, y, z, 0, blockStem.getDamage());
                    return true;
                }
            }

            return false;
        }
    }

    private static class GroupedUpdaters implements Updater {
        private final Updater[] updaters;

        public GroupedUpdaters(Updater... updaters) {
            this.updaters = updaters;
        }

        @Override
        public boolean update(int offsetX, int offsetY, int offsetZ, int x, int y, int z, int blockId, int meta) {
            for (Updater updater : updaters) {
                if (updater != null && updater.update(offsetX, offsetY, offsetZ, x, y, z, blockId, meta)) {
                    return true;
                }
            }
            return false;
        }
    }
}
