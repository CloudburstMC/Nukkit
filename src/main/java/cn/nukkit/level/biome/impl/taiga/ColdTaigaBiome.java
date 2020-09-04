package cn.nukkit.level.biome.impl.taiga;

import cn.nukkit.api.RemovedFromNewRakNet;
import cn.nukkit.api.Since;
import cn.nukkit.level.generator.populator.impl.WaterIcePopulator;

/**
 * @author DaPorkchop_ (Nukkit Project)
 */
public class ColdTaigaBiome extends TaigaBiome {
    public ColdTaigaBiome() {
        super();

        WaterIcePopulator ice = new WaterIcePopulator();
        this.addPopulator(ice);

        this.setBaseHeight(0.2f);
        this.setHeightVariation(0.2f);
    }

    @Override
    public String getName() {
        return "Cold Taiga";
    }

    @Since("1.4.0.0-PN")
    @Override
    @RemovedFromNewRakNet
    public int getCoverBlock() {
        if (useNewRakNetCover()) {
            return getCoverId(0,0) >> 4;
        }
        return SNOW_LAYER;
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
