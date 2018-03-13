package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.helper.EnsureBelow;
import cn.nukkit.level.generator.populator.helper.EnsureCover;
import cn.nukkit.level.generator.populator.helper.EnsureGrassBelow;
import cn.nukkit.level.generator.populator.type.PopulatorSurfaceBlock;
import cn.nukkit.math.NukkitRandom;

/**
 * @author DaPorkchop_
 */
public class PopulatorSmallMushroom extends PopulatorSurfaceBlock {

    @Override
    protected boolean canStay(int x, int y, int z, FullChunk chunk) {
        return EnsureCover.ensureCover(x, y, z, chunk) && EnsureGrassBelow.ensureGrassBelow(x, y, z, chunk);
    }

    @Override
    protected int getBlockId(int x, int z, NukkitRandom random, FullChunk chunk) {
        return BROWN_MUSHROOM << 4;
    }
}
