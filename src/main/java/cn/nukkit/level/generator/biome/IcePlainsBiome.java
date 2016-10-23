package cn.nukkit.level.generator.biome;

import cn.nukkit.block.BlockSapling;
import cn.nukkit.level.generator.populator.PopulatorTallGrass;
import cn.nukkit.level.generator.populator.PopulatorTree;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class IcePlainsBiome extends SnowyBiome {

    public IcePlainsBiome() {
        super();
        PopulatorTallGrass tallGrass = new PopulatorTallGrass();
        tallGrass.setBaseAmount(5);

        PopulatorTree trees = new PopulatorTree(BlockSapling.SPRUCE);
        trees.setBaseAmount(1);
        trees.setRandomAmount(1);

        this.addPopulator(tallGrass);
        this.addPopulator(trees);
        this.setElevation(63, 74);
        this.temperature = 0D;
        this.rainfall = 0.5D;
    }

    public String getName() {
        return "Ice Plains";
    }
}
