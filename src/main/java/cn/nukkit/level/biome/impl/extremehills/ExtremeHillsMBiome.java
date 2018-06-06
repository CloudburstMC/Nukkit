package cn.nukkit.level.biome.impl.extremehills;

import cn.nukkit.level.generator.noise.nukkit.f.SimplexF;
import cn.nukkit.math.NukkitRandom;

/**
 * author: DaPorkchop_
 * Nukkit Project
 * <p>
 * make sure this is touching another extreme hills type or it'll look dumb
 *
 * very smooth hills with flat areas between
 */
public class ExtremeHillsMBiome extends ExtremeHillsPlusBiome {
    private static final SimplexF gravelNoise = new SimplexF(new NukkitRandom(0), 1f, 1 / 4f, 1 / 64f);
    private boolean isGravel = false;

    public ExtremeHillsMBiome() {
        this(true);
    }

    public ExtremeHillsMBiome(boolean tree) {
        super(tree);

        this.setBaseHeight(1f);
        this.setHeightVariation(0.5f);
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
        isGravel = gravelNoise.noise2D(x, z, true) < -0.75f;
    }

    @Override
    public boolean doesOverhang() {
        return false;
    }
}
