package cn.nukkit.level.biome.impl.taiga;

import cn.nukkit.level.generator.populator.WaterIcePopulator;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class ColdTaigaHillsBiome extends ColdTaigaBiome {
    public ColdTaigaHillsBiome() {
        super();

        this.setElevation(70, 90);
    }

    @Override
    public String getName() {
        return "Cold Taiga Hills";
    }
}
