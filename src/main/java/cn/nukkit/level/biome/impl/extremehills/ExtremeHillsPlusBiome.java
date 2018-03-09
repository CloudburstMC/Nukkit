package cn.nukkit.level.biome.impl.extremehills;

import cn.nukkit.level.biome.type.GrassyBiome;

/**
 * author: DaPorkchop_
 * Nukkit Project
 *
 * make sure this is touching another extreme hills type or it'll look dumb
 */
public class ExtremeHillsPlusBiome extends ExtremeHillsBiome {

    public ExtremeHillsPlusBiome() {
        this(true);
    }

    public ExtremeHillsPlusBiome(boolean tree) {
        super(tree);

        this.setElevation(85, 140);

        this.temperature = 0.4;
        this.rainfall = 0.5;
    }

    @Override
    public String getName() {
        return "Extreme Hills+";
    }
}
