package cn.nukkit.level.generator.biome;

import cn.nukkit.level.generator.populator.PopulatorGrass;
import cn.nukkit.level.generator.populator.PopulatorTallGrass;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PlainBiome extends GrassyBiome {

    public PlainBiome() {
        super();

        PopulatorGrass grass = new PopulatorGrass();
        grass.setBaseAmount(40);
        PopulatorTallGrass tallGrass = new PopulatorTallGrass();
        tallGrass.setBaseAmount(7);
        //PopulatorFlower flower = new PopulatorFlower();
        //flower.setBaseAmount(10);

        this.addPopulator(grass);
        this.addPopulator(tallGrass);
        //this.addPopulator(flower);

        this.setElevation(63, 74);

        this.temperature = 0.8;
        this.rainfall = 0.4;
    }

    @Override
    public String getName() {
        return "Plains";
    }
}
