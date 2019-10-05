package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.generator.populator.helper.EnsureBelow;
import cn.nukkit.level.generator.populator.helper.EnsureCover;
import cn.nukkit.level.generator.populator.type.PopulatorSurfaceBlock;
import cn.nukkit.math.NukkitRandom;

/**
 * @author DaPorkchop_
 */
public class PopulatorGroundFire extends PopulatorSurfaceBlock {
    @Override
    protected boolean canStay(int x, int y, int z, Chunk chunk, ChunkManager level) {
        return EnsureCover.ensureCover(x, y, z, chunk) && EnsureBelow.ensureBelow(x, y, z, NETHERRACK, chunk);
    }

    @Override
    protected int getBlockId(int x, int z, NukkitRandom random, Chunk chunk) {
        return FIRE << 4;
    }

    @Override
    protected void placeBlock(int x, int y, int z, int id, Chunk chunk, NukkitRandom random) {
        super.placeBlock(x, y, z, id, chunk, random);
        chunk.setBlockLight(x, y, z, Block.light[FIRE]);
    }

    @Override
    protected int getHighestWorkableBlock(ChunkManager level, int x, int z, Chunk chunk) {
        int y;
        for (y = 0; y <= 127; ++y) {
            int b = chunk.getBlockId(x, y, z);
            if (b == Block.AIR) {
                break;
            }
        }
        return y == 0 ? -1 : y;
    }
}
