package cn.nukkit.level.biome.impl.taiga;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.generator.populator.impl.WaterIcePopulator;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class ColdTaigaBiome extends TaigaBiome {

    public ColdTaigaBiome() {
        super();

        final WaterIcePopulator ice = new WaterIcePopulator();
        this.addPopulator(ice);

        this.setBaseHeight(0.2f);
        this.setHeightVariation(0.2f);
    }

    @Override
    public String getName() {
        return "Cold Taiga";
    }

    @Override
    public int getCoverBlock() {
        return BlockID.SNOW_LAYER;
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
