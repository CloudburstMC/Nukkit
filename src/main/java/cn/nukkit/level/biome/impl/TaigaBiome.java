package cn.nukkit.level.biome.impl;

import cn.nukkit.block.BlockSapling;
import cn.nukkit.level.biome.type.SnowyBiome;
import cn.nukkit.level.generator.populator.PopulatorTree;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class TaigaBiome extends SnowyBiome {

    public TaigaBiome() {
        super();

        PopulatorTree trees = new PopulatorTree(BlockSapling.SPRUCE);
        trees.setBaseAmount(10);
        this.addPopulator(trees);

        this.setElevation(67, 70);

        this.temperature = 0.05;
        this.rainfall = 0.8;
    }

    @Override
    public String getName() {
        return "Taiga";
    }
}
