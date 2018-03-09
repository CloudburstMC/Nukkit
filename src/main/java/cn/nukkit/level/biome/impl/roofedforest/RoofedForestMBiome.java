package cn.nukkit.level.biome.impl.roofedforest;

import cn.nukkit.level.biome.type.GrassyBiome;
import cn.nukkit.level.generator.populator.MushroomPopulator;
import cn.nukkit.level.generator.populator.PopulatorFlower;
import cn.nukkit.level.generator.populator.tree.DarkOakTreePopulator;

public class RoofedForestMBiome extends RoofedForestBiome {

    public RoofedForestMBiome() {
        super();

        this.setElevation(67, 85);
    }

    @Override
    public String getName() {
        return "Roofed Forest M";
    }
}
