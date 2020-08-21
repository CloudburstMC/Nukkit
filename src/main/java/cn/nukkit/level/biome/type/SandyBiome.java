package cn.nukkit.level.biome.type;

import cn.nukkit.block.Block;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class SandyBiome extends CoveredBiome {
    @Override
    public int getSurfaceDepth(int x, int y, int z) {
        return 3;
    }

    @Override
    public int getSurfaceId(int x, int y, int z) {
        return SAND << Block.DATA_BITS;
    }

    @Override
    public int getGroundDepth(int x, int y, int z) {
        return 2;
    }

    @Override
    public int getGroundId(int x, int y, int z) {
        return SANDSTONE << Block.DATA_BITS;
    }
}
