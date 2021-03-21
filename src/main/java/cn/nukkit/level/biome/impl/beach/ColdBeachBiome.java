package cn.nukkit.level.biome.impl.beach;

import cn.nukkit.api.RemovedFromNewRakNet;
import cn.nukkit.api.Since;
import cn.nukkit.level.biome.type.SandyBiome;
import cn.nukkit.level.generator.populator.impl.WaterIcePopulator;

public class ColdBeachBiome extends SandyBiome {
    public ColdBeachBiome() {
        WaterIcePopulator ice = new WaterIcePopulator();
        this.addPopulator(ice);

        this.setBaseHeight(0f);
        this.setHeightVariation(0.025f);
    }

    @Since("1.4.0.0-PN")
    @Override
    @RemovedFromNewRakNet
    public int getCoverBlock() {
        if (useNewRakNetCover()) {
            return getCoverId(0,0) >> 4; 
        }
        return SNOW_LAYER;
    }

    @Override
    public String getName() {
        return "Cold Beach";
    }

    @Override
    public boolean isFreezing() {
        return true;
    }

    @Override
    public boolean canRain() {
        return false;
    }
}
