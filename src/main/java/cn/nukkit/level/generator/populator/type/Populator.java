package cn.nukkit.level.generator.populator.type;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.math.NukkitRandom;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Populator implements BlockID {
    public abstract void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, Chunk chunk);

    protected int getHighestWorkableBlock(ChunkManager level, int x, int z, Chunk chunk) {
        return chunk.getHighestBlock(x & 0xF, z & 0xF);
    }
}
