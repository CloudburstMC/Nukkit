package cn.nukkit.level.format;

import cn.nukkit.entity.Entity;
import cn.nukkit.tile.Tile;

import java.util.TreeMap;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface FullChunk {
    //todo

    public abstract int getX();

    public abstract int getZ();

    public abstract void setX(int x);

    public abstract void setZ(int z);

    public abstract LevelProvider getProvider();

    public abstract void setProvider(LevelProvider provider);

    public abstract int getFullBlock(int x, int y, int z);

    public abstract boolean setBlock(int x, int y, int z);

    public abstract boolean setBlock(int x, int y, int z, Integer blockId);

    public abstract boolean setBlock(int x, int y, int z, Integer blockId, Integer meta);

    public abstract int getBlockId(int x, int y, int z);

    public abstract void setBlockId(int x, int y, int z, int id);

    public abstract int getBlockData(int x, int y, int z);

    public abstract void setBlockData(int x, int y, int z, int data);

    public abstract int getBlockSkyLight(int x, int y, int z);

    public abstract void setBlockSkyLight(int x, int y, int z, int level);

    public abstract int getBlockLight(int x, int y, int z);

    public abstract void setBlockLight(int x, int y, int z, int level);

    public abstract int getHighestBlockAt(int x, int z);

    public abstract int getHighestBlockAt(int x, int z, boolean cache);

    public abstract int getHeightMap(int x, int z);

    public abstract void setHeightMap(int x, int z, int value);

    public abstract void recalculateHeightMap();

    public abstract void populateSkyLight();

    public abstract int getBiomeId(int x, int z);

    public abstract void setBiomeId(int x, int z, int biomeId);

    public abstract int[] getBiomeColor(int x, int z);

    public abstract byte[] getBlockIdColumn(int x, int z);

    public abstract byte[] getBlockDataColumn(int x, int z);

    public abstract byte[] getBlockSkyLightColumn(int x, int z);

    public abstract byte[] getBlockLightColumn(int x, int z);

    public abstract void setBiomeColor(int x, int z, int R, int G, int B);

    public abstract boolean isLightPopulated();

    public abstract void setLightPopulated();

    public abstract void setLightPopulated(boolean value);

    public abstract boolean isPopulated();

    public abstract void setPopulated();

    public abstract void setPopulated(boolean value);

    public abstract boolean isGenerated();

    public abstract void setGenerated();

    public abstract void setGenerated(boolean value);

    public abstract void addEntity(Entity entity);

    public abstract void removeEntity(Entity entity);

    public abstract void addTile(Tile tile);

    public abstract void removeTile(Tile tile);

    public abstract TreeMap<Integer, Entity> getEntities();

    public abstract TreeMap<Integer, Tile> getTiles();

    public abstract Tile getTile(int x, int y, int z);

    public abstract boolean isLoaded();

    public abstract boolean load();

    public abstract boolean load(boolean generate);

    public abstract boolean unload();

    public abstract boolean unload(boolean save);

    public abstract boolean unload(boolean save, boolean safe);

    public abstract void initChunk();

    public abstract byte[] getBiomeIdArray();

    public abstract int[] getBiomeColorArray();

    public abstract int[] getHeightMapArray();

    public abstract byte[] getBlockIdArray();

    public abstract byte[] getBlockDataArray();

    public abstract byte[] getBlockSkyLightArray();

    public abstract byte[] getBlockLightArray();

    public abstract byte[] toBinary() throws Exception;

    public abstract byte[] toFastBinary() throws Exception;

    public abstract boolean hasChanged();

    public abstract void setChanged();

    public abstract void setChanged(boolean changed);

    /*public abstract FullChunk fromBinary(byte[] data);

    public abstract FullChunk fromBinary(byte[] data, LevelProvider provider);

    public abstract FullChunk fromFastBinary(byte[] data);

    public abstract FullChunk fromFastBinary(byte[] data, LevelProvider provider);

    public abstract FullChunk getEmptyChunk(int chunkX, int chunkZ);

    public abstract FullChunk getEmptyChunk(int chunkX, int chunkZ, LevelProvider provider);*/
}
