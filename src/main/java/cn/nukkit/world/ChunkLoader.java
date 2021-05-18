package cn.nukkit.world;

import cn.nukkit.math.Vector3;
import cn.nukkit.world.format.FullChunk;

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

    World getWorld();

    void onChunkChanged(FullChunk chunk);

    void onChunkLoaded(FullChunk chunk);

    void onChunkUnloaded(FullChunk chunk);

    void onChunkPopulated(FullChunk chunk);

    void onBlockChanged(Vector3 block);
}
