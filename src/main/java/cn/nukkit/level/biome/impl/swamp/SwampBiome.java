package cn.nukkit.level.biome.impl.swamp;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFlower;
import cn.nukkit.level.biome.type.GrassyBiome;
import cn.nukkit.level.generator.populator.impl.*;
import cn.nukkit.level.generator.populator.impl.tree.SwampTreePopulator;

import java.util.Arrays;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class SwampBiome extends GrassyBiome {

    public SwampBiome() {
        super();

        PopulatorUnderwaterFloor underwaterFloorClay = new PopulatorUnderwaterFloor(1.0, CLAY_BLOCK, 1, 2, 1, Arrays.asList(DIRT, CLAY_BLOCK));
        underwaterFloorClay.setBaseAmount(1);
        addPopulator(underwaterFloorClay);

        PopulatorSeagrass populatorSeagrass = new PopulatorSeagrass();
        populatorSeagrass.setBaseAmount(24);
        populatorSeagrass.setRandomAmount(24);
        addPopulator(populatorSeagrass);

        PopulatorLilyPad lilypad = new PopulatorLilyPad();
        lilypad.setBaseAmount(4);
        lilypad.setRandomAmount(2);
        this.addPopulator(lilypad);

        SwampTreePopulator trees = new SwampTreePopulator();
        trees.setBaseAmount(2);
        this.addPopulator(trees);

        PopulatorFlower flower = new PopulatorFlower();
        flower.setBaseAmount(2);
        flower.addType(Block.RED_FLOWER, BlockFlower.TYPE_BLUE_ORCHID);
        this.addPopulator(flower);

        MushroomPopulator mushroom = new MushroomPopulator(1);
        mushroom.setBaseAmount(-5);
        mushroom.setRandomAmount(7);
        this.addPopulator(mushroom);

        PopulatorSmallMushroom smallMushroom = new PopulatorSmallMushroom();
        smallMushroom.setRandomAmount(2);
        this.addPopulator(smallMushroom);

        this.setBaseHeight(-0.2f);
        this.setHeightVariation(0.1f);
    }

    @Override
    public String getName() {
        return "Swamp";
    }
}
