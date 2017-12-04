package cn.nukkit.server.level.generator.biome;

import cn.nukkit.server.block.BlockSapling;
import cn.nukkit.server.level.generator.populator.PopulatorFlower;
import cn.nukkit.server.level.generator.populator.PopulatorGrass;
import cn.nukkit.server.level.generator.populator.PopulatorTallGrass;
import cn.nukkit.server.level.generator.populator.tree.SavannaTreePopulator;

public class SavannaBiome extends GrassyBiome {

    public SavannaBiome() {
        super();
        SavannaTreePopulator tree = new SavannaTreePopulator(BlockSapling.ACACIA);
        tree.setBaseAmount(1);
        PopulatorTallGrass tallGrass = new PopulatorTallGrass();
        tallGrass.setBaseAmount(20);

        PopulatorGrass grass = new PopulatorGrass();
        grass.setBaseAmount(20);

        PopulatorFlower flower = new PopulatorFlower();
        flower.setBaseAmount(4);

        this.addPopulator(tallGrass);
        this.addPopulator(grass);
        this.addPopulator(tree);
        this.addPopulator(flower);

        this.setElevation(62, 68);
        this.temperature = 1.2f;
        this.rainfall = 0.0f;
    }

    @Override
    public String getName() {
        return "Savanna";
    }
}
