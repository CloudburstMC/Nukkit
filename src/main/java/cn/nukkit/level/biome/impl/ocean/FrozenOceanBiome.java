package cn.nukkit.level.biome.impl.ocean;

import cn.nukkit.level.generator.populator.WaterIcePopulator;

/**
 * author: DaPorkchop_
 * Nukkit Project
 *
 * This biome does not generate naturally
 */
public class FrozenOceanBiome extends OceanBiome {
    public FrozenOceanBiome() {
        super();

        WaterIcePopulator ice = new WaterIcePopulator();
        this.addPopulator(ice);
    }

    @Override
    public String getName() {
        return "Frozen Ocean";
    }

    @Override
    public boolean isFreezing() {
        return true;
    }
}
