package cn.nukkit.level.biome.impl.taiga;

import cn.nukkit.block.BlockDoublePlant;
import cn.nukkit.block.BlockFlower;
import cn.nukkit.block.BlockSapling;
import cn.nukkit.level.biome.type.GrassyBiome;
import cn.nukkit.level.generator.object.tree.ObjectTree;
import cn.nukkit.level.generator.populator.impl.PopulatorDoublePlant;
import cn.nukkit.level.generator.populator.impl.PopulatorFlower;
import cn.nukkit.level.generator.populator.impl.PopulatorSweetBerryBush;
import cn.nukkit.level.generator.populator.impl.PopulatorTree;

/**
 * @author DaPorkchop_
 * Nukkit Project
 */
public class TaigaBiome extends GrassyBiome {
    public TaigaBiome() {
        super();

        PopulatorTree trees = new PopulatorTree(BlockSapling.SPRUCE + ObjectTree.SNOWY_TREE);
        trees.setBaseAmount(10);
        this.addPopulator(trees);

        if (!(this instanceof ColdTaigaBiome)) {
            PopulatorDoublePlant tallGrass = new PopulatorDoublePlant(BlockDoublePlant.LARGE_FERN);
            tallGrass.setBaseAmount(2);
            this.addPopulator(tallGrass);

            PopulatorSweetBerryBush bush = new PopulatorSweetBerryBush();
            bush.setRandomAmount(3);
            this.addPopulator(bush);
        }

        PopulatorFlower flower = new PopulatorFlower();
        flower.setRandomAmount(3);
        flower.addType(DANDELION, 0);
        flower.addType(RED_FLOWER, BlockFlower.TYPE_POPPY);
        this.addPopulator(flower);

        this.setBaseHeight(0.2f);
        this.setHeightVariation(0.2f);
    }

    @Override
    public String getName() {
        return "Taiga";
    }
}
