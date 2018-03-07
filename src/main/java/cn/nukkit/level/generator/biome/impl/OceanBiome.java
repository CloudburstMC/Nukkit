package cn.nukkit.level.generator.biome.impl;

import cn.nukkit.level.generator.biome.type.WateryBiome;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class OceanBiome extends WateryBiome {

    public OceanBiome() {
        this.setElevation(46, 59);

        this.temperature = 0.5;
        this.rainfall = 0.5;

    }

    @Override
    public String getName() {
        return "Ocean";
    }
}
