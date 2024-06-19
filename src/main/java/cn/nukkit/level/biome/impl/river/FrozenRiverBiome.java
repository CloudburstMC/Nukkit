package cn.nukkit.level.biome.impl.river;

import cn.nukkit.block.Block;

/**
 * @author DaPorkchop_
 * Nukkit Project
 */
public class FrozenRiverBiome extends RiverBiome {
    public FrozenRiverBiome() {
        super();
    }

    @Override
    public String getName() {
        return "Frozen River";
    }

    @Override
    public boolean isFreezing() {
        return true;
    }

    @Override
    public boolean canRain() {
        return false;
    }

    @Override
    public int getCoverId(int x, int z) {
        return SNOW_LAYER << Block.DATA_BITS;
    }
}
