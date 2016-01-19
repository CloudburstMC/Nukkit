package cn.nukkit.level;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface ChunkLoader {

    Integer getLoaderId();

    boolean isLoaderActive();

    Location getPosition();

    double getX();

    double getZ();

    Level getLevel();

    void onChunkChanged(FullChunk chunk);

    void onChunkLoaded(FullChunk chunk);

    void onChunkUnloaded(FullChunk chunk);

    void onChunkPopulated(FullChunk chunk);

    void onBlockChanged(Vector3 block);
}
