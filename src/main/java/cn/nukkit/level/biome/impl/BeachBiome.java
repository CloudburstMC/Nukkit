package cn.nukkit.level.biome.impl;

import cn.nukkit.level.biome.type.SandyBiome;
import cn.nukkit.level.generator.populator.PopulatorSugarcane;

/**
 * Author: PeratX
 * Nukkit Project
 */
public class BeachBiome extends SandyBiome {
    public BeachBiome() {
        PopulatorSugarcane sugarcane = new PopulatorSugarcane();
        sugarcane.setBaseAmount(0);
        sugarcane.setRandomAmount(3);
        this.addPopulator(sugarcane);

        this.setElevation(64, 67);
        this.temperature = 2;
        this.rainfall = 2;
    }

    @Override
    public String getName() {
        return "Beach";
    }
}
