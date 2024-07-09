package cn.nukkit.level.biome.impl.ocean;

import cn.nukkit.block.Block;
import cn.nukkit.level.generator.populator.impl.PopulatorSeagrass;

public class WarmOceanBiome extends OceanBiome {

    public WarmOceanBiome() {
        PopulatorSeagrass populatorSeagrass = new PopulatorSeagrass();
        populatorSeagrass.setBaseAmount(24);
        populatorSeagrass.setRandomAmount(24);
        this.addPopulator(populatorSeagrass);

        this.setBaseHeight(-1.0f);
        this.setHeightVariation(0.1f);
    }

    @Override
    public String getName() {
        return "Warm Ocean";
    }

    @Override
    public int getGroundId(int x, int y, int z) {
        return Block.SAND << Block.DATA_BITS;
    }
}
