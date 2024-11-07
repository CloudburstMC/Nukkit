package cn.nukkit.level.biome.impl.taiga;

import cn.nukkit.block.Block;

/**
 * @author DaPorkchop_
 * Nukkit Project
 */
public class ColdTaigaBiome extends TaigaBiome {
    public ColdTaigaBiome() {
        super();

        this.setBaseHeight(0.2f);
        this.setHeightVariation(0.2f);
    }

    @Override
    public String getName() {
        return "Cold Taiga";
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
