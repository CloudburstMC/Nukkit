package cn.nukkit.level.biome.type;

import cn.nukkit.block.BlockID;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public abstract class WateryBiome extends CoveredBiome {

    @Override
    public int getSurfaceDepth(final int y) {
        return 0;
    }

    @Override
    public int getSurfaceBlock(final int y) {
        //doesn't matter, surface depth is 0
        return 0;
    }

    @Override
    public int getGroundDepth(final int y) {
        return 5;
    }

    @Override
    public int getGroundBlock(final int y) {
        return BlockID.DIRT;
    }

}
