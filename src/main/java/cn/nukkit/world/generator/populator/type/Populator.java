package cn.nukkit.world.generator.populator.type;

import cn.nukkit.block.BlockID;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.world.ChunkManager;
import cn.nukkit.world.format.FullChunk;

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
