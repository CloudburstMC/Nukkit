package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.populator.helper.PopulatorHelpers;
import cn.nukkit.level.generator.populator.type.PopulatorSurfaceBlock;
import cn.nukkit.math.NukkitRandom;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class PopulatorGrass extends PopulatorSurfaceBlock {
    private static final Block TALL_GRASS = Block.get(BlockIds.TALL_GRASS, 1);

    @Override
    protected boolean canStay(int x, int y, int z, IChunk chunk, ChunkManager level) {
        return PopulatorHelpers.canGrassStay(x, y, z, chunk);
    }

    @Override
    protected Block getBlock(int x, int z, NukkitRandom random, IChunk chunk) {
        return TALL_GRASS;
    }
}
