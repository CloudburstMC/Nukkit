package cn.nukkit.level.generator.biome;

import cn.nukkit.block.Block;
import cn.nukkit.level.generator.populator.PopulatorSugarcane;
import cn.nukkit.level.generator.populator.PopulatorTallSugarcane;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class OceanBiome extends WateryBiome {

    public OceanBiome() {
        super();

        this.setElevation(46, 63);

        this.temperature = 0.5;
        this.rainfall = 0.5;

    }

    @Override
    public Block[] getGroundCover() {
        return super.getGroundCover();
    }

    @Override
    public String getName() {
        return "Ocean";
    }
}
