package cn.nukkit.level.biome.impl.extremehills;

import cn.nukkit.level.generator.noise.Simplex;
import cn.nukkit.math.NukkitRandom;

/**
 * author: DaPorkchop_
 * Nukkit Project
 * <p>
 * make sure this is touching another extreme hills type or it'll look dumb
 */
//porktodo: this has no overhangs!
public class ExtremeHillsMBiome extends ExtremeHillsPlusBiome {
    private static final Simplex gravelNoise = new Simplex(new NukkitRandom(0), 1d, 1 / 4d, 1 / 3.5d);
    private boolean isGravel = false;

    public ExtremeHillsMBiome() {
        this(true);
    }

    public ExtremeHillsMBiome(boolean tree) {
        super(tree);
    }

    @Override
    public String getName() {
        return "Extreme Hills M";
    }

    @Override
    public int getSurfaceBlock(int y) {
        return isGravel ? GRAVEL : super.getSurfaceBlock(y);
    }

    @Override
    public int getSurfaceDepth(int y) {
        return isGravel ? 4 : super.getSurfaceDepth(y);
    }

    @Override
    public int getGroundDepth(int y) {
        return isGravel ? 0 : super.getGroundDepth(y);
    }

    @Override
    public void preCover(int x, int z) {
        //-0.75 is farily rare, so there'll be much more gravel than grass
        isGravel = gravelNoise.noise2D(x, z, true) < -0.75;
    }
}
