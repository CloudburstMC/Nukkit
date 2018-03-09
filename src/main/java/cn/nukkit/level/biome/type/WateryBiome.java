package cn.nukkit.level.biome.type;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public abstract class WateryBiome extends CoveredBiome {
    @Override
    public int getSurfaceDepth(int y) {
        return 0;
    }

    @Override
    public int getSurfaceBlock(int y) {
        //doesn't matter, surface depth is 0
        return 0;
    }

    @Override
    public int getGroundDepth(int y) {
        return 5;
    }

    @Override
    public int getGroundBlock(int y) {
        return DIRT;
    }
}
