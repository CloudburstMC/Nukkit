package cn.nukkit.level.biome.impl.ocean;

import cn.nukkit.block.Block;
import cn.nukkit.level.biome.type.WateryBiome;
import cn.nukkit.level.generator.populator.impl.PopulatorKelp;
import cn.nukkit.level.generator.populator.impl.PopulatorSeagrass;
import cn.nukkit.level.generator.populator.impl.PopulatorUnderwaterFloor;

import java.util.Arrays;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class OceanBiome extends WateryBiome {

    public OceanBiome() {
        PopulatorUnderwaterFloor underwaterFloor = new PopulatorUnderwaterFloor(1.0, SAND, 2, 4, 2, Arrays.asList(GRASS, DIRT));
        underwaterFloor.setBaseAmount(3);
        this.addPopulator(underwaterFloor);

        PopulatorUnderwaterFloor underwaterFloorClay = new PopulatorUnderwaterFloor(1.0, CLAY_BLOCK, 1, 2, 1, Arrays.asList(DIRT, CLAY_BLOCK));
        underwaterFloorClay.setBaseAmount(1);
        this.addPopulator(underwaterFloorClay);

        PopulatorUnderwaterFloor underwaterFloorGravel = new PopulatorUnderwaterFloor(1.0, GRAVEL, 2, 3, 2, Arrays.asList(GRASS, DIRT));
        underwaterFloorGravel.setBaseAmount(1);
        this.addPopulator(underwaterFloorGravel);

        if (!(this instanceof FrozenOceanBiome)) {
            PopulatorKelp populatorKelp = new PopulatorKelp();
            populatorKelp.setBaseAmount(-135);
            populatorKelp.setRandomAmount(180);
            this.addPopulator(populatorKelp);
        }

        PopulatorSeagrass populatorSeagrass = new PopulatorSeagrass();
        populatorSeagrass.setBaseAmount(24);
        populatorSeagrass.setRandomAmount(24);
        this.addPopulator(populatorSeagrass);

        this.setBaseHeight(-1.0f);
        this.setHeightVariation(0.1f);
    }

    @Override
    public String getName() {
        return "Ocean";
    }

    @Override
    public int getGroundId(int x, int y, int z) {
        return Block.GRAVEL << Block.DATA_BITS;
    }

    @Override
    public int getSurfaceDepth(int x, int y, int z) {
        return 1;
    }

    @Override
    public int getSurfaceId(int x, int y, int z) {
        return SAND << Block.DATA_BITS;
    }
}
