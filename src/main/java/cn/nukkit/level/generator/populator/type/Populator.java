package cn.nukkit.level.generator.populator.type;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.math.BedrockRandom;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Populator {
    public abstract void populate(ChunkManager level, int chunkX, int chunkZ, BedrockRandom random, IChunk chunk);

    protected int getHighestWorkableBlock(ChunkManager level, int x, int z, IChunk chunk) {
        return chunk.getHighestBlock(x & 0xF, z & 0xF);
    }
}
