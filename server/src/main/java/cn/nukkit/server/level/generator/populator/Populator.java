package cn.nukkit.server.level.generator.populator;

import cn.nukkit.server.level.ChunkManager;
import cn.nukkit.server.math.NukkitRandom;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Populator {
    public abstract void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random);
}
