package cn.nukkit.world.generator.populator.impl;

import cn.nukkit.math.NukkitRandom;
import cn.nukkit.world.format.FullChunk;
import cn.nukkit.world.generator.populator.helper.EnsureBelow;
import cn.nukkit.world.generator.populator.helper.EnsureCover;
import cn.nukkit.world.generator.populator.type.PopulatorSurfaceBlock;

/**
 * @author DaPorkchop_
 */
public class PopulatorLilyPad extends PopulatorSurfaceBlock {

    @Override
    protected boolean canStay(int x, int y, int z, FullChunk chunk) {
        return EnsureCover.ensureCover(x, y, z, chunk) && EnsureBelow.ensureBelow(x, y, z, STILL_WATER, chunk);
    }

    @Override
    protected int getBlockId(int x, int z, NukkitRandom random, FullChunk chunk) {
        return WATER_LILY << 4;
    }
}
