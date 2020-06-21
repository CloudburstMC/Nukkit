package cn.nukkit.level.biome.impl.forest;

import cn.nukkit.block.BlockDoublePlant;
import cn.nukkit.block.BlockFlower;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.generator.populator.impl.PopulatorFlower;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class FlowerForestBiome extends ForestBiome {

    public FlowerForestBiome() {
        this(ForestBiome.TYPE_NORMAL);
    }

    public FlowerForestBiome(final int type) {
        super(type);

        //see https://minecraft.gamepedia.com/Flower#Flower_biomes
        final PopulatorFlower flower = new PopulatorFlower();
        flower.setBaseAmount(10);
        flower.addType(BlockID.DANDELION, 0);
        flower.addType(BlockID.RED_FLOWER, BlockFlower.TYPE_POPPY);
        flower.addType(BlockID.RED_FLOWER, BlockFlower.TYPE_ALLIUM);
        flower.addType(BlockID.RED_FLOWER, BlockFlower.TYPE_AZURE_BLUET);
        flower.addType(BlockID.RED_FLOWER, BlockFlower.TYPE_RED_TULIP);
        flower.addType(BlockID.RED_FLOWER, BlockFlower.TYPE_ORANGE_TULIP);
        flower.addType(BlockID.RED_FLOWER, BlockFlower.TYPE_WHITE_TULIP);
        flower.addType(BlockID.RED_FLOWER, BlockFlower.TYPE_PINK_TULIP);
        flower.addType(BlockID.RED_FLOWER, BlockFlower.TYPE_OXEYE_DAISY);
        flower.addType(BlockID.DOUBLE_PLANT, BlockDoublePlant.LILAC);
        flower.addType(BlockID.DOUBLE_PLANT, BlockDoublePlant.ROSE_BUSH);
        flower.addType(BlockID.DOUBLE_PLANT, BlockDoublePlant.PEONY);
        this.addPopulator(flower);

        this.setHeightVariation(0.4f);
    }

    @Override
    public String getName() {
        return this.type == ForestBiome.TYPE_BIRCH ? "Birch Forest" : "Forest";
    }

}
