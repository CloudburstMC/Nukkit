package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.BlockID;
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

    @Override
    protected boolean canStay(final int x, final int y, final int z, final FullChunk chunk) {
        return EnsureCover.ensureCover(x, y, z, chunk) && (EnsureGrassBelow.ensureGrassBelow(x, y, z, chunk) || EnsureBelow.ensureBelow(x, y, z, BlockID.SAND, chunk)) && this.findWater(x, y - 1, z, chunk.getProvider().getLevel());
    }

    @Override
    protected int getBlockId(final int x, final int z, final NukkitRandom random, final FullChunk chunk) {
        return BlockID.SUGARCANE_BLOCK << 4 | 1;
    }

    private boolean findWater(final int x, final int y, final int z, final Level level) {
        int count = 0;
        for (int i = x - 4; i < x + 4; i++) {
            for (int j = z - 4; j < z + 4; j++) {
                final int b = level.getBlockIdAt(i, y, j);
                if (b == BlockID.WATER || b == BlockID.STILL_WATER) {
                    count++;
                }
                if (count > 10) {
                    return true;
                }
            }
        }
        return count > 10;
    }

}
