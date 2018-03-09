package cn.nukkit.level.biome.impl.desert;

import cn.nukkit.level.biome.type.SandyBiome;
import cn.nukkit.level.generator.populator.PopulatorCactus;
import cn.nukkit.level.generator.populator.PopulatorDeadBush;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class DesertMBiome extends DesertBiome {
    public DesertMBiome() {
        super();

        this.setElevation(63, 71);
    }

    @Override
    public String getName() {
        return "Desert M";
    }
}
