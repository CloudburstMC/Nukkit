package cn.nukkit.world.generator.populator.impl;

import cn.nukkit.math.NukkitRandom;
import cn.nukkit.world.format.FullChunk;
import cn.nukkit.world.generator.populator.helper.EnsureCover;
import cn.nukkit.world.generator.populator.helper.EnsureGrassBelow;
import cn.nukkit.world.generator.populator.type.PopulatorSurfaceBlock;

/**
 * @author DaPorkchop_
 */
public class PopulatorMelon extends PopulatorSurfaceBlock {
    @Override
    protected boolean canStay(int x, int y, int z, FullChunk chunk) {
        return EnsureCover.ensureCover(x, y, z, chunk) && EnsureGrassBelow.ensureGrassBelow(x, y, z, chunk);
    }

    @Override
    protected int getBlockId(int x, int z, NukkitRandom random, FullChunk chunk) {
        return MELON_BLOCK << 4;
    }
}
