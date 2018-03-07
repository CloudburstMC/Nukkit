package cn.nukkit.level.generator.populator;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.NukkitRandom;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Populator {
    public abstract void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk);
}
