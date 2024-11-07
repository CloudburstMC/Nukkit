package cn.nukkit.level.biome.impl.river;

import cn.nukkit.block.Block;
import cn.nukkit.level.biome.type.WateryBiome;
import cn.nukkit.level.generator.populator.impl.PopulatorSeagrass;
import cn.nukkit.level.generator.populator.impl.PopulatorSugarcane;
import cn.nukkit.level.generator.populator.impl.PopulatorTallSugarcane;
import cn.nukkit.level.generator.populator.impl.PopulatorUnderwaterFloor;

import java.util.Arrays;

/**
 * @author DaPorkchop_
 * Nukkit Project
 */
public class RiverBiome extends WateryBiome {

    public RiverBiome() {
        PopulatorUnderwaterFloor underwaterFloorSand = new PopulatorUnderwaterFloor(1.0, SAND, 2, 4, 2, Arrays.asList(GRASS, DIRT));
        underwaterFloorSand.setBaseAmount(3);
        addPopulator(underwaterFloorSand);

        PopulatorUnderwaterFloor underwaterFloorClay = new PopulatorUnderwaterFloor(1.0, CLAY_BLOCK, 1, 2, 1, Arrays.asList(DIRT, CLAY_BLOCK));
        underwaterFloorClay.setBaseAmount(1);
        addPopulator(underwaterFloorClay);

        PopulatorUnderwaterFloor underwaterFloorGravel = new PopulatorUnderwaterFloor(1.0, GRAVEL, 2, 3, 2, Arrays.asList(GRASS, DIRT));
        underwaterFloorGravel.setBaseAmount(1);
        addPopulator(underwaterFloorGravel);

        PopulatorSeagrass populatorSeagrass = new PopulatorSeagrass();
        populatorSeagrass.setBaseAmount(24);
        populatorSeagrass.setRandomAmount(24);
        addPopulator(populatorSeagrass);

        PopulatorSugarcane sugarcane = new PopulatorSugarcane();
        sugarcane.setRandomAmount(3);
        this.addPopulator(sugarcane);

        PopulatorTallSugarcane tallSugarcane = new PopulatorTallSugarcane();
        tallSugarcane.setRandomAmount(1);
        this.addPopulator(tallSugarcane);

        this.setBaseHeight(-0.5f);
        this.setHeightVariation(0f);
    }

    @Override
    public String getName() {
        return "River";
    }

    @Override
    public int getSurfaceDepth(int x, int y, int z) {
        return 1;
    }

    @Override
    public int getSurfaceId(int x, int y, int z) {
        return GRASS << Block.DATA_BITS;
    }
}
