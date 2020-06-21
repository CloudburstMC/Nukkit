package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.helper.EnsureBelow;
import cn.nukkit.level.generator.populator.helper.EnsureCover;
import cn.nukkit.level.generator.populator.type.PopulatorSurfaceBlock;
import cn.nukkit.math.NukkitRandom;

/**
 * @author DaPorkchop_
 */
public class PopulatorGroundFire extends PopulatorSurfaceBlock {

    @Override
    protected boolean canStay(final int x, final int y, final int z, final FullChunk chunk) {
        return EnsureCover.ensureCover(x, y, z, chunk) && EnsureBelow.ensureBelow(x, y, z, BlockID.NETHERRACK, chunk);
    }

    @Override
    protected int getBlockId(final int x, final int z, final NukkitRandom random, final FullChunk chunk) {
        return BlockID.FIRE << 4;
    }

    @Override
    protected int getHighestWorkableBlock(final ChunkManager level, final int x, final int z, final FullChunk chunk) {
        int y;
        for (y = 0; y <= 127; ++y) {
            final int b = chunk.getBlockId(x, y, z);
            if (b == BlockID.AIR) {
                break;
            }
        }
        return y == 0 ? -1 : y;
    }

    @Override
    protected void placeBlock(final int x, final int y, final int z, final int id, final FullChunk chunk, final NukkitRandom random) {
        super.placeBlock(x, y, z, id, chunk, random);
        chunk.setBlockLight(x, y, z, Block.light[BlockID.FIRE]);
    }

}
