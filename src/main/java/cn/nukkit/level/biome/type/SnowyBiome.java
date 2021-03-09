package cn.nukkit.level.biome.type;

import cn.nukkit.level.generator.populator.impl.WaterIcePopulator;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public abstract class SnowyBiome extends GrassyBiome {
    public SnowyBiome() {
        super();

        WaterIcePopulator waterIce = new WaterIcePopulator();
        this.addPopulator(waterIce);
    }

    @Override
    public int getCoverId(int x, int z) {
        return SNOW_LAYER << 4;
    }

    @Override
    public boolean canRain() {
        return false;
    }
}
