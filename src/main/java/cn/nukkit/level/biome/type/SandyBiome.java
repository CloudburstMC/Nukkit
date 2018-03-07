package cn.nukkit.level.biome.type;

import cn.nukkit.block.Block;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class SandyBiome extends CoveredBiome {

    @Override
    public int getSurfaceDepth() {
        return 3;
    }

    @Override
    public int getSurfaceBlock() {
        return Block.SAND;
    }

    @Override
    public int getGroundDepth() {
        return 2;
    }

    @Override
    public int getGroundBlock() {
        return Block.SANDSTONE;
    }
}
