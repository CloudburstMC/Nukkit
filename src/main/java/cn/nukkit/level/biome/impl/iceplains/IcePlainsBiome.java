package cn.nukkit.level.biome.impl.iceplains;

import cn.nukkit.block.BlockSapling;
import cn.nukkit.level.biome.type.SnowyBiome;
import cn.nukkit.level.generator.populator.impl.PopulatorTree;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class IcePlainsBiome extends SnowyBiome {

    public IcePlainsBiome() {
        super();

        PopulatorTree trees = new PopulatorTree(BlockSapling.SPRUCE);
        trees.setBaseAmount(0);
        trees.setRandomAmount(1);
        this.addPopulator(trees);



        this.setBaseHeight(0.125f);
        this.setHeightVariation(0.05f);
    }

    public String getName() {
        return "Ice Plains";
    }
}
