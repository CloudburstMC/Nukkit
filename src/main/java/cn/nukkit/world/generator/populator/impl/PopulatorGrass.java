package cn.nukkit.world.generator.populator.impl;

import cn.nukkit.math.NukkitRandom;
import cn.nukkit.world.format.FullChunk;
import cn.nukkit.world.generator.populator.helper.PopulatorHelpers;
import cn.nukkit.world.generator.populator.type.PopulatorSurfaceBlock;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class PopulatorGrass extends PopulatorSurfaceBlock {
    @Override
    protected boolean canStay(int x, int y, int z, FullChunk chunk) {
        return PopulatorHelpers.canGrassStay(x, y, z, chunk);
    }

    @Override
    protected int getBlockId(int x, int z, NukkitRandom random, FullChunk chunk) {
        return (TALL_GRASS << 4) | 1;
    }
}
