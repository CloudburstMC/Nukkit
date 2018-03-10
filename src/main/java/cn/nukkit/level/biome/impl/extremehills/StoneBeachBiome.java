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
        this.setElevation(80, 90);
    }

    @Override
    public int getSurfaceDepth(int y) {
        return 0;
    }

    @Override
    public int getSurfaceBlock(int y) {
        return 0;
    }

    @Override
    public int getGroundDepth(int y) {
        return 0;
    }

    @Override
    public int getGroundBlock(int y) {
        return 0;
    }

    @Override
    public String getName() {
        return "Stone Beach";
    }
}
