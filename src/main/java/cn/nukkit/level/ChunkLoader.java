package cn.nukkit.level;

import cn.nukkit.level.chunk.Chunk;
import com.nukkitx.math.vector.Vector3f;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface ChunkLoader {

    int getLoaderId();

    boolean isLoaderActive();

    Vector3f getPosition();

    float getX();

    float getZ();

    Level getLevel();

    void onChunkChanged(Chunk chunk);

    void onChunkLoaded(Chunk chunk);

    void onChunkUnloaded(Chunk chunk);
}
