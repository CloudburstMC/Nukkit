package cn.nukkit.level.biome.impl.river;

import cn.nukkit.level.biome.type.WateryBiome;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class RiverBiome extends WateryBiome {

    public RiverBiome() {
        this.setElevation(58, 62);

        this.temperature = 0.5;
        this.rainfall = 0.7;
    }

    @Override
    public String getName() {
        return "River";
    }
}
