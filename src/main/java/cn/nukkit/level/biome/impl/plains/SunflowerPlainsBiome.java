package cn.nukkit.level.biome.impl.plains;

import cn.nukkit.block.BlockDoublePlant;
import cn.nukkit.block.BlockSapling;
import cn.nukkit.level.biome.type.GrassyBiome;
import cn.nukkit.level.generator.populator.impl.PopulatorDoublePlant;
import cn.nukkit.level.generator.populator.impl.PopulatorTree;

/**
 * @author DaPorkchop_
 * Nukkit Project
 */
public class SunflowerPlainsBiome extends GrassyBiome {

    public SunflowerPlainsBiome() {
        super();

        PopulatorTree trees = new PopulatorTree(BlockSapling.OAK);
        trees.setRandomAmount(1);
        this.addPopulator(trees);

        PopulatorDoublePlant sunflower = new PopulatorDoublePlant(BlockDoublePlant.SUNFLOWER);
        sunflower.setBaseAmount(8);
        sunflower.setRandomAmount(5);
        this.addPopulator(sunflower);

        this.setBaseHeight(0.125f);
        this.setHeightVariation(0.05f);
    }

    @Override
    public String getName() {
        return "Sunflower Plains";
    }
}
