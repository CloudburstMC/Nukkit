package cn.nukkit.world.biome.impl.river;

import cn.nukkit.world.biome.type.WateryBiome;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class RiverBiome extends WateryBiome {

    public RiverBiome() {
        this.setBaseHeight(-0.5f);
        this.setHeightVariation(0f);
    }

    @Override
    public String getName() {
        return "River";
    }
}
