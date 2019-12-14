package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.generator.populator.helper.PopulatorHelpers;
import cn.nukkit.level.generator.populator.type.PopulatorSurfaceBlock;
import cn.nukkit.math.NukkitRandom;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class PopulatorGrass extends PopulatorSurfaceBlock {
    private static final Block TALL_GRASS = Block.get(BlockID.TALL_GRASS, 1);
    @Override
    protected boolean canStay(int x, int y, int z, Chunk chunk, ChunkManager level) {
        return PopulatorHelpers.canGrassStay(x, y, z, chunk);
    }

    @Override
    protected Block getBlock(int x, int z, NukkitRandom random, Chunk chunk) {
        return TALL_GRASS;
    }
}
