package cn.nukkit.level.biome.impl.extremehills;

import cn.nukkit.level.generator.populator.PopulatorTree;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class ExtremeHillsEdgeBiome extends ExtremeHillsBiome {

    public ExtremeHillsEdgeBiome() {
        super();

        this.setElevation(67, 85);
    }

    @Override
    public String getName() {
        return "Extreme Hills Edge";
    }
}
