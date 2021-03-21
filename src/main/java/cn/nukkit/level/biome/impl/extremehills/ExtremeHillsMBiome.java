package cn.nukkit.level.biome.impl.extremehills;

import cn.nukkit.api.NewRakNetOnly;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.generator.noise.nukkit.f.SimplexF;
import cn.nukkit.math.NukkitRandom;

/**
 * @author DaPorkchop_ (Nukkit Project)
 * <p>
 * make sure this is touching another extreme hills type or it'll look dumb
 *
 * very smooth hills with flat areas between
 */
public class ExtremeHillsMBiome extends ExtremeHillsPlusBiome {
    private static final BlockState STATE_GRAVEL = BlockState.of(GRAVEL);
    private static final BlockState STATE_GRASS = BlockState.of(GRASS);
    private static final SimplexF gravelNoise = new SimplexF(new NukkitRandom(0), 1f, 1 / 4f, 1 / 64f);

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

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    public BlockState getSurfaceState(int x, int y, int z) {
        return gravelNoise.noise2D(x, z, true) < -0.75f ? STATE_GRAVEL : STATE_GRASS;
    }

    @NewRakNetOnly
    @Override
    public int getSurfaceDepth(int x, int y, int z) {
        return gravelNoise.noise2D(x, z, true) < -0.75f ? 4 : 1;
    }
    
    @NewRakNetOnly
    @Override
    public int getGroundDepth(int x, int y, int z) {
        return gravelNoise.noise2D(x, z, true) < -0.75f ? 0 : 4;
    }

    @Override
    public boolean doesOverhang() {
        return false;
    }
}
