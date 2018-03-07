package cn.nukkit.level.biome.impl;

import cn.nukkit.block.BlockSapling;
import cn.nukkit.level.biome.type.GrassyBiome;
import cn.nukkit.level.generator.populator.PopulatorFlower;
import cn.nukkit.level.generator.populator.tree.SavannaTreePopulator;

public class SavannaBiome extends GrassyBiome {

    public SavannaBiome() {
        super();
        SavannaTreePopulator tree = new SavannaTreePopulator(BlockSapling.ACACIA);
        tree.setBaseAmount(1);

        PopulatorFlower flower = new PopulatorFlower();
        flower.setBaseAmount(4);

        this.addPopulator(tree);
        this.addPopulator(flower);

        this.setElevation(67, 69);
        this.temperature = 1.2f;
        this.rainfall = 0.0f;
    }

    @Override
    public String getName() {
        return "Savanna";
    }
}
