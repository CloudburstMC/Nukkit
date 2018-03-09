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
public class ExtremeHillsPlusMBiome extends ExtremeHillsMBiome {

    public ExtremeHillsPlusMBiome() {
        super(false);

        this.setElevation(70, 140);
    }

    @Override
    public String getName() {
        return "Extreme Hills+ M";
    }
}
