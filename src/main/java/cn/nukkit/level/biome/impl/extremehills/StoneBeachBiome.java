package cn.nukkit.level.biome.impl.extremehills;

import cn.nukkit.level.biome.type.CoveredBiome;

/**
 * @author DaPorkchop_
 * <p>
 * Occurs when Extreme hills and variants touch the ocean.
 *
 * Nearly ertical cliffs, but no overhangs. Height difference is 2-7 near ocean, and pretty much flat everywhere else
 */
public class StoneBeachBiome extends CoveredBiome {
    public StoneBeachBiome() {
        this.setBaseHeight(0.1f);
        this.setHeightVariation(0.8f);
    }

    @Override
    public int getSurfaceDepth(int x, int y, int z) {
        return 0;
    }

    @Override
    public int getSurfaceId(int x, int y, int z) {
        return 0;
    }

    @Override
    public int getGroundDepth(int x, int y, int z) {
        return 0;
    }

    @Override
    public int getGroundId(int x, int y, int z) {
        return 0;
    }

    @Override
    public String getName() {
        return "Stone Beach";
    }
}
