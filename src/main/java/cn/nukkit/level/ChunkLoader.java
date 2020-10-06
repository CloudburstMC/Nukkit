package cn.nukkit.level;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public interface ChunkLoader {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    ChunkLoader[] EMPTY_ARRAY = new ChunkLoader[0];

    int getLoaderId();

    boolean isLoaderActive();

    Position getPosition();

    double getX();

    double getZ();

    Level getLevel();

    void onChunkChanged(FullChunk chunk);

    void onChunkLoaded(FullChunk chunk);

    void onChunkUnloaded(FullChunk chunk);

    void onChunkPopulated(FullChunk chunk);

    void onBlockChanged(Vector3 block);
}
