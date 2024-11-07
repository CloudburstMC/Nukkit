package cn.nukkit.level.biome.impl.ocean;

import cn.nukkit.block.Block;

public class LukewarmOceanBiome extends OceanBiome {

    public LukewarmOceanBiome() {
        super();
    }

    @Override
    public String getName() {
        return "Lukewarm Ocean";
    }

    @Override
    public int getGroundId(int x, int y, int z) {
        return Block.SAND << Block.DATA_BITS;
    }
}
