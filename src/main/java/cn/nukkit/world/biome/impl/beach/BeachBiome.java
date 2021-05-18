package cn.nukkit.world.biome.impl.beach;

import cn.nukkit.world.biome.type.SandyBiome;
import cn.nukkit.world.generator.populator.impl.PopulatorSugarcane;

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

        this.setBaseHeight(0f);
        this.setHeightVariation(0.025f);
    }

    @Override
    public String getName() {
        return "Beach";
    }
}
