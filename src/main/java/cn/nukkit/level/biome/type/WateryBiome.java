package cn.nukkit.level.biome.type;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public abstract class WateryBiome extends CoveredBiome {
    @Override
    public int getSurfaceDepth(int x, int y, int z) {
        return 0;
    }

    @Override
    public int getSurfaceId(int x, int y, int z) {
        //doesn't matter, surface depth is 0
        return 0;
    }

    @Override
    public int getGroundDepth(int x, int y, int z) {
        return 5;
    }

    @Override
    public int getGroundId(int x, int y, int z) {
        return DIRT << 4;
    }
}
