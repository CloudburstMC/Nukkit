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
        return this.getSurfaceBlock(0, y, 0);
    }

    @Override
    public int getSurfaceBlock(int x, int y, int z) {
        return gravelNoise.noise2D(x, z, true) < -0.75f ? GRAVEL : super.getSurfaceBlock(x, y, z);
    }

    @Override
    public int getSurfaceDepth(int y) {
        return this.getSurfaceDepth(0, y, 0);
    }

    @Override
    public int getSurfaceDepth(int x, int y, int z) {
        return gravelNoise.noise2D(x, z, true) < -0.75f ? 4 : super.getSurfaceDepth(x, y, z);
    }

    @Override
    public int getGroundDepth(int y) {
        return this.getGroundDepth(0, y, 0);
    }

    @Override
    public int getGroundDepth(int x, int y, int z) {
        return gravelNoise.noise2D(x, z, true) < -0.75f ? 0 : super.getGroundDepth(x, y, z);
    }

    @Override
    public boolean doesOverhang() {
        return false;
    }
}
