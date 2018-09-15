package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.helper.EnsureBelow;
import cn.nukkit.level.generator.populator.helper.EnsureCover;
import cn.nukkit.level.generator.populator.helper.EnsureGrassBelow;
import cn.nukkit.level.generator.populator.type.PopulatorSurfaceBlock;
import cn.nukkit.math.NukkitRandom;

/**
 * @author Niall Lindsay (Niall7459)
 * <p>
 * Nukkit Project
 * </p>
 */
public class PopulatorSugarcane extends PopulatorSurfaceBlock {

    private boolean findWater(int x, int y, int z, Level level) {
        int count = 0;
        for (int i = x - 4; i < (x + 4); i++) {
            for (int j = z - 4; j < (z + 4); j++) {
                int b = level.getBlockIdAt(i, y, j);
                if (b == Block.WATER || b == Block.STILL_WATER) {
                    count++;
                }
                if (count > 10) {
                    return true;
                }
            }
        }
        return (count > 10);
    }

    @Override
    protected boolean canStay(int x, int y, int z, FullChunk chunk) {
        return EnsureCover.ensureCover(x, y, z, chunk) && (EnsureGrassBelow.ensureGrassBelow(x, y, z, chunk) || EnsureBelow.ensureBelow(x, y, z, SAND, chunk)) && findWater(x, y - 1, z, chunk.getProvider().getLevel());
    }

    @Override
    protected int getBlockId(int x, int z, NukkitRandom random, FullChunk chunk) {
        return (SUGARCANE_BLOCK << 4) | 1;
    }
}
