package cn.nukkit.server.level.format;

import cn.nukkit.server.blockentity.BlockEntity;

import java.io.IOException;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface FullChunk extends Cloneable {

    int getX();

    int getZ();

    void setX(int x);

    void setZ(int z);

    LevelProvider getProvider();

    void setProvider(LevelProvider provider);

    int getFullBlock(int x, int y, int z);

    boolean setBlock(int x, int y, int z);

    boolean setBlock(int x, int y, int z, Integer blockId);

    boolean setBlock(int x, int y, int z, Integer blockId, Integer meta);

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

    void setBiomeId(int x, int z, int biomeId);

    int[] getBiomeColor(int x, int z);

    byte[] getBlockIdColumn(int x, int z);

    byte[] getBlockDataColumn(int x, int z);

    byte[] getBlockSkyLightColumn(int x, int z);

    byte[] getBlockLightColumn(int x, int z);

    void setBiomeColor(int x, int z, int r, int g, int b);

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

    Map<Long, Entity> getEntities();

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

    int[] getBiomeColorArray();

    int[] getHeightMapArray();

    byte[] getBlockIdArray();

    byte[] getBlockDataArray();

    Map<Integer, Integer> getBlockExtraDataArray();

    byte[] getBlockSkyLightArray();

    byte[] getBlockLightArray();

    byte[] toBinary();

    byte[] toFastBinary();

    boolean hasChanged();

    void setChanged();

    void setChanged(boolean changed);

}
