package cn.nukkit.level.biome.impl.roofedforest;

import cn.nukkit.level.biome.type.GrassyBiome;
import cn.nukkit.level.generator.populator.MushroomPopulator;
import cn.nukkit.level.generator.populator.PopulatorFlower;
import cn.nukkit.level.generator.populator.tree.DarkOakTreePopulator;

public class RoofedForestBiome extends GrassyBiome {

    public RoofedForestBiome() {
        super();

        DarkOakTreePopulator tree = new DarkOakTreePopulator();
        tree.setBaseAmount(20);
        tree.setRandomAmount(10);
        this.addPopulator(tree);

        PopulatorFlower flower = new PopulatorFlower();
        flower.setBaseAmount(2);
        this.addPopulator(flower);

        MushroomPopulator mushroom = new MushroomPopulator();
        mushroom.setBaseAmount(0);
        mushroom.setRandomAmount(1);
        this.addPopulator(mushroom);

        this.setElevation(67, 73);

        this.temperature = 0.7f;
        this.rainfall = 0.8f;
    }

    @Override
    public String getName() {
        return "Roofed Forest";
    }

}
