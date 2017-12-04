package cn.nukkit.server.level.generator.biome;

import cn.nukkit.server.level.generator.populator.MushroomPopulator;
import cn.nukkit.server.level.generator.populator.PopulatorFlower;
import cn.nukkit.server.level.generator.populator.PopulatorGrass;
import cn.nukkit.server.level.generator.populator.tree.DarkOakTreePopulator;

public class RoofedForestBiome extends GrassyBiome {

    public RoofedForestBiome() {
        super();
        DarkOakTreePopulator tree = new DarkOakTreePopulator();
        tree.setBaseAmount(30);

        PopulatorGrass grass = new PopulatorGrass();
        grass.setBaseAmount(10);

        PopulatorFlower flower = new PopulatorFlower();
        flower.setBaseAmount(2);

        MushroomPopulator mushroom = new MushroomPopulator();
        mushroom.setBaseAmount(0);
        mushroom.setRandomAmount(1);

        this.addPopulator(mushroom);
        this.addPopulator(grass);
        this.addPopulator(tree);
        this.addPopulator(flower);

        this.setElevation(62, 68);
        this.temperature = 0.7f;
        this.rainfall = 0.8f;
    }

    @Override
    public String getName() {
        return "Roofed Forest";
    }

}
