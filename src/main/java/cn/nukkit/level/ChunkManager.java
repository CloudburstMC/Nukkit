package cn.nukkit.level;

import cn.nukkit.level.chunk.Chunk;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface ChunkManager {

    int getBlockIdAt(int x, int y, int z);

    void setBlockFullIdAt(int x, int y, int z, int fullId);

    void setBlockIdAt(int x, int y, int z, int id);

    default void setBlockAt(int x, int y, int z, int id) {
        setBlockAt(x, y, z, id, 0);
    }

    default void setBlockAt(int x, int y, int z, int id, int data) {
        this.setBlockFullIdAt(x, y, z, id << 4 | data);
    }

    int getBlockDataAt(int x, int y, int z);

    void setBlockDataAt(int x, int y, int z, int data);

    Chunk getChunk(int chunkX, int chunkZ);

    long getSeed();
}
