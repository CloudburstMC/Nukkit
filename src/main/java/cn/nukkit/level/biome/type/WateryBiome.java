package cn.nukkit.level.biome.type;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public abstract class WateryBiome extends CoveredBiome {
    @Override
    public int getSurfaceDepth() {
        return 0;
    }

    @Override
    public int getSurfaceBlock() {
        //doesn't matter, surface depth is 0
        return 0;
    }

    @Override
    public int getGroundDepth() {
        return 5;
    }

    @Override
    public int getGroundBlock() {
        return DIRT;
    }
}
