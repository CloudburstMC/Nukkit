package cn.nukkit.level.biome.impl.ocean;

import cn.nukkit.level.biome.type.WateryBiome;

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
