package cn.nukkit.level.biome.type;

import cn.nukkit.block.BlockID;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class SandyBiome extends CoveredBiome {

    @Override
    public int getSurfaceDepth(final int y) {
        return 3;
    }

    @Override
    public int getSurfaceBlock(final int y) {
        return BlockID.SAND;
    }

    @Override
    public int getGroundDepth(final int y) {
        return 2;
    }

    @Override
    public int getGroundBlock(final int y) {
        return BlockID.SANDSTONE;
    }

}
