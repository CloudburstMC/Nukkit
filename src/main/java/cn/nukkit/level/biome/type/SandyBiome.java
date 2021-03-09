package cn.nukkit.level.biome.type;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class SandyBiome extends CoveredBiome {
    @Override
    public int getSurfaceDepth(int x, int y, int z) {
        return 3;
    }

    @Override
    public int getSurfaceId(int x, int y, int z) {
        return SAND << 4;
    }

    @Override
    public int getGroundDepth(int x, int y, int z) {
        return 2;
    }

    @Override
    public int getGroundId(int x, int y, int z) {
        return SANDSTONE << 4;
    }
}
