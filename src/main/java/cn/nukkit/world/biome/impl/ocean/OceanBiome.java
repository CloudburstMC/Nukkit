package cn.nukkit.world.biome.impl.ocean;

import cn.nukkit.world.biome.type.WateryBiome;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class OceanBiome extends WateryBiome {

    public OceanBiome() {
        this.setBaseHeight(-1f);
        this.setHeightVariation(0.1f);
    }

    @Override
    public String getName() {
        return "Ocean";
    }

    public int getGroundId(int y) {
        return GRAVEL << 4;
    }
}
