package cn.nukkit.level.biome.impl.taiga;

import cn.nukkit.block.BlockSapling;
import cn.nukkit.level.biome.type.GrassyBiome;
import cn.nukkit.level.generator.populator.PopulatorTree;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
//porktodo: this should be flat-ish in most places, and upheavals should be steep
public class TaigaMBiome extends TaigaBiome {
    public TaigaMBiome() {
        super();

        this.setElevation(67, 90);
    }

    @Override
    public String getName() {
        return "Taiga M";
    }
}
