package cn.nukkit.level.biome.type;

import cn.nukkit.api.RemovedFromNewRakNet;
import cn.nukkit.api.Since;
import cn.nukkit.level.generator.populator.impl.WaterIcePopulator;

/**
 * @author DaPorkchop_ (Nukkit Project)
 */
public abstract class SnowyBiome extends GrassyBiome {
    public SnowyBiome() {
        super();

        WaterIcePopulator waterIce = new WaterIcePopulator();
        this.addPopulator(waterIce);
    }

    @Since("1.4.0.0-PN")
    @Override
    @RemovedFromNewRakNet
    public int getCoverBlock() {
        if (useNewRakNetCover()) {
            return getCoverId(0,0);
        }
        return SNOW_LAYER;
    }

    @Override
    public boolean canRain() {
        return false;
    }
}
