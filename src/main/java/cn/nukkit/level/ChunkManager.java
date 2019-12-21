package cn.nukkit.level;

import cn.nukkit.level.format.generic.BaseFullChunk;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface ChunkManager {

    int getBlockIdAt(int x, int y, int z, int layer);
    int getBlockIdAt(int x, int y, int z);

    void setBlockFullIdAt(int x, int y, int z, int layer, int fullId);

    void setBlockFullIdAt(int x, int y, int z, int fullId);

    void setBlockIdAt(int x, int y, int z, int layer, int id);
    void setBlockIdAt(int x, int y, int z, int id);

    boolean setBlockAtLayer(int x, int y, int z, int layer, int id, int data);

    default boolean setBlockAtLayer(int x, int y, int z, int layer, int id) {
        return setBlockAtLayer(x, y, z, layer, id, 0);
    }

    default void setBlockAt(int x, int y, int z, int id) {
        setBlockAt(x, y, z, id, 0);
    }

    void setBlockAt(int x, int y, int z, int id, int data);

    int getBlockDataAt(int x, int y, int z, int layer);
    int getBlockDataAt(int x, int y, int z);

    void setBlockDataAt(int x, int y, int z, int layer, int data);
    void setBlockDataAt(int x, int y, int z, int data);

    BaseFullChunk getChunk(int chunkX, int chunkZ);

    void setChunk(int chunkX, int chunkZ);

    void setChunk(int chunkX, int chunkZ, BaseFullChunk chunk);

    long getSeed();
}
