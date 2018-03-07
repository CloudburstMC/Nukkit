package cn.nukkit.level.biome.impl;

import cn.nukkit.level.biome.type.GrassyBiome;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ExtremeHillsBiome extends GrassyBiome {

    public ExtremeHillsBiome() {
        super();

        this.setElevation(67, 127);

        this.temperature = 0.4;
        this.rainfall = 0.5;
    }

    @Override
    public String getName() {
        return "Extreme Hills";
    }
}
