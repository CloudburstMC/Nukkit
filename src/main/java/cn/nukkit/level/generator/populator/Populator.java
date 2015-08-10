package cn.nukkit.level.generator.populator;

import cn.nukkit.level.ChunkManager;

import java.util.Random;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Populator {
    public abstract void populate(ChunkManager level, int chunkX, int chunkZ, Random random);
}
