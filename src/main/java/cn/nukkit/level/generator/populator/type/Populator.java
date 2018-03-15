package cn.nukkit.level.generator.populator.type;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.NukkitRandom;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Populator implements BlockID {
    public abstract void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk);

    protected int getHighestWorkableBlock(ChunkManager level, int x, int z, FullChunk chunk)    {
        return chunk.getHighestBlockAt(x & 0xF, z & 0xF);
    }
}
