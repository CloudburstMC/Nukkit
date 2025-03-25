package cn.nukkit.level.biome.impl.extremehills;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFlower;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockSapling;
import cn.nukkit.level.biome.type.GrassyBiome;
import cn.nukkit.level.generator.object.ore.OreType;
import cn.nukkit.level.generator.populator.impl.PopulatorFlower;
import cn.nukkit.level.generator.populator.impl.PopulatorOre;
import cn.nukkit.level.generator.populator.impl.PopulatorTree;

/**
 * @author DaPorkchop_
 * Nukkit Project
 * <p>
 * make sure this is touching another extreme hills type or it'll look dumb
 *
 * steep mountains with flat areas between
 */
public class ExtremeHillsBiome extends GrassyBiome {
    public ExtremeHillsBiome() {
        this(true);
    }

    public ExtremeHillsBiome(boolean tree) {
        super();

        if (tree) {
            PopulatorTree trees = new PopulatorTree(BlockSapling.SPRUCE);
            trees.setBaseAmount(2);
            trees.setRandomAmount(2);
            this.addPopulator(trees);
        }

        this.addPopulator(new PopulatorOre(STONE, new OreType[]{
                new OreType(Block.get(BlockID.EMERALD_ORE), 11, 1, 0, 32),
                new OreType(Block.get(BlockID.MONSTER_EGG), 7, 9, 0, 63)
        }));

        PopulatorFlower flower = new PopulatorFlower();
        flower.setRandomAmount(3);
        flower.addType(DANDELION, 0);
        flower.addType(RED_FLOWER, BlockFlower.TYPE_POPPY);
        this.addPopulator(flower);

        this.setBaseHeight(1f);
        this.setHeightVariation(0.5f);
    }

    @Override
    public String getName() {
        return "Extreme Hills";
    }

    @Override
    public boolean doesOverhang() {
        return true;
    }
}
