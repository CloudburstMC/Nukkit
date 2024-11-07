package cn.nukkit.level.biome.type;

import cn.nukkit.block.Block;

/**
 * @author DaPorkchop_
 * Nukkit Project
 */
public abstract class SnowyBiome extends GrassyBiome {
    public SnowyBiome() {
        super();
    }

    @Override
    public int getCoverId(int x, int z) {
        return Block.SNOW_LAYER << Block.DATA_BITS;
    }

    @Override
    public boolean isFreezing() {
        return true;
    }

    @Override
    public boolean canRain() {
        return false;
    }
}
