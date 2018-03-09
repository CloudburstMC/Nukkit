package cn.nukkit.level.biome.impl.taiga;

import cn.nukkit.level.generator.populator.WaterIcePopulator;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class ColdTaigaBiome extends TaigaBiome {
    public ColdTaigaBiome() {
        super();

        WaterIcePopulator ice = new WaterIcePopulator();
        this.addPopulator(ice);

        this.setElevation(67, 70);
    }

    @Override
    public String getName() {
        return "Cold Taiga";
    }

    @Override
    public int getCoverBlock() {
        return SNOW_LAYER;
    }

    @Override
    public boolean isFreezing() {
        return true;
    }
}
