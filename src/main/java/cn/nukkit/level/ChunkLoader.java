package cn.nukkit.level;

import cn.nukkit.level.chunk.Chunk;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface ChunkLoader {

    int getLoaderId();

    boolean isLoaderActive();

    Position getPosition();

    double getX();

    double getZ();

    Level getLevel();

    void onChunkChanged(Chunk chunk);

    void onChunkLoaded(Chunk chunk);

    void onChunkUnloaded(Chunk chunk);
}
