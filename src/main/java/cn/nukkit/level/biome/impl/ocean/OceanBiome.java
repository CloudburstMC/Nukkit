package cn.nukkit.level.biome.impl.ocean;

import cn.nukkit.api.RemovedFromNewRakNet;
import cn.nukkit.api.Since;
import cn.nukkit.level.biome.type.WateryBiome;

/**
 * @author MagicDroidX (Nukkit Project)
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

    @RemovedFromNewRakNet
    @Since("1.4.0.0-PN")
    @Override
    public int getGroundBlock(int y) {
        if (useNewRakNetGround()) {
            return getGroundId(0,y,0) >> 4;
        }
        return GRAVEL;
    }
}
