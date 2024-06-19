package cn.nukkit.level.biome.impl.plains;

import cn.nukkit.block.BlockSapling;
import cn.nukkit.level.biome.type.GrassyBiome;
import cn.nukkit.level.generator.populator.impl.PopulatorPumpkin;
import cn.nukkit.level.generator.populator.impl.PopulatorTree;

/**
 * @author DaPorkchop_
 * Nukkit Project
 */
public class PlainsBiome extends GrassyBiome {

    public PlainsBiome() {
        super();

        PopulatorTree trees = new PopulatorTree(BlockSapling.OAK);
        trees.setRandomAmount(1);
        this.addPopulator(trees);

        this.addPopulator(new PopulatorPumpkin());

        this.setBaseHeight(0.125f);
        this.setHeightVariation(0.05f);
    }

    @Override
    public String getName() {
        return "Plains";
    }
}
