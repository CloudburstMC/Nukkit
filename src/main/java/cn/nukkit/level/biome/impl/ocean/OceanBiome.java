package cn.nukkit.level.biome.impl.ocean;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.biome.type.WateryBiome;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class OceanBiome extends WateryBiome {

    public OceanBiome() {
        this.setBaseHeight(-1f);
        this.setHeightVariation(0.1f);
    }

    @Override
    public String getName() {
        return "Ocean";
    }

    @Override
    public int getGroundBlock(final int y) {
        return BlockID.GRAVEL;
    }

}
