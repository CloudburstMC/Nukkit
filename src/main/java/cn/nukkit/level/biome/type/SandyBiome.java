package cn.nukkit.level.biome.type;

import cn.nukkit.block.Block;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class SandyBiome extends CoveredBiome {

    @Override
    public int getSurfaceDepth(int y) {
        return 3;
    }

    @Override
    public int getSurfaceBlock(int y) {
        return Block.SAND;
    }

    @Override
    public int getGroundDepth(int y) {
        return 2;
    }

    @Override
    public int getGroundBlock(int y) {
        return Block.SANDSTONE;
    }
}
