package cn.nukkit.level.biome.impl;

import cn.nukkit.level.biome.type.WateryBiome;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class RiverBiome extends WateryBiome {

    public RiverBiome() {
        super();
        this.setElevation(58, 62);

        this.temperature = 0.5;
        this.rainfall = 0.7;
    }

    @Override
    public String getName() {
        return "River";
    }
}
