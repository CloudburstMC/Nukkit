package cn.nukkit.level.format;

import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import cn.nukkit.level.biome.Biome;

import java.io.IOException;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface FullChunk extends Cloneable {

    int getX();

    int getZ();

    default void setPosition(int x, int z) {
        setX(x);
        setZ(z);
    }

    void setX(int x);

    void setZ(int z);

    long getIndex();

    LevelProvider getProvider();

    void setProvider(LevelProvider provider);

    int getFullBlock(int x, int y, int z);

    Block getAndSetBlock(int x, int y, int z, Block block);

    default boolean setFullBlockId(int x, int y, int z, int fullId) {
        return setBlock(x, y, z, fullId >> 4, fullId & 0xF);
    }

    boolean setBlock(int x, int y, int z, int  blockId);

    boolean setBlock(int x, int y, int z, int  blockId, int  meta);

    int getBlockId(int x, int y, int z);

    void setBlockId(int x, int y, int z, int id);

    int getBlockData(int x, int y, int z);

    void setBlockData(int x, int y, int z, int data);

    int getBlockExtraData(int x, int y, int z);

    void setBlockExtraData(int x, int y, int z, int data);

    int getBlockSkyLight(int x, int y, int z);

    void setBlockSkyLight(int x, int y, int z, int level);

    int getBlockLight(int x, int y, int z);

    void setBlockLight(int x, int y, int z, int level);

    int getHighestBlockAt(int x, int z);

    int getHighestBlockAt(int x, int z, boolean cache);

    int getHeightMap(int x, int z);

    void setHeightMap(int x, int z, int value);

    void recalculateHeightMap();

    void populateSkyLight();

    int getBiomeId(int x, int z);

    void setBiomeId(int x, int z, byte biomeId);

    default void setBiomeId(int x, int z, int biomeId)  {
        setBiomeId(x, z, (byte) biomeId);
    }

    default void setBiome(int x, int z, Biome biome) {
        setBiomeId(x, z, (byte) biome.getId());
    }

    boolean isLightPopulated();

    void setLightPopulated();

    void setLightPopulated(boolean value);

    boolean isPopulated();

    void setPopulated();

    void setPopulated(boolean value);

    boolean isGenerated();

    void setGenerated();

    void setGenerated(boolean value);

    void addEntity(Entity entity);

    void removeEntity(Entity entity);

    void addBlockEntity(BlockEntity blockEntity);

    void removeBlockEntity(BlockEntity blockEntity);

    Long2ObjectMap<Entity> getEntities();

    Map<Long, BlockEntity> getBlockEntities();

    BlockEntity getTile(int x, int y, int z);

    boolean isLoaded();

    boolean load() throws IOException;

    boolean load(boolean generate) throws IOException;

    boolean unload() throws Exception;

    boolean unload(boolean save) throws Exception;

    boolean unload(boolean save, boolean safe) throws Exception;

    void initChunk();

    byte[] getBiomeIdArray();

    byte[] getHeightMapArray();

    byte[] getBlockIdArray();

    byte[] getBlockDataArray();

    Map<Integer, Integer> getBlockExtraDataArray();

    byte[] getBlockSkyLightArray();

    byte[] getBlockLightArray();

    byte[] toBinary();

    byte[] toFastBinary();

    boolean hasChanged();

    void markDirty();

    void markDirty(boolean changed);

    boolean shouldDoRandomTick(Level level);
}
