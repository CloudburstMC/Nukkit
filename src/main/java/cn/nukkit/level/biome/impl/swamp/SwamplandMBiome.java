package cn.nukkit.level.biome.impl.swamp;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFlower;
import cn.nukkit.level.biome.type.GrassyBiome;
import cn.nukkit.level.generator.populator.PopulatorFlower;
import cn.nukkit.level.generator.populator.PopulatorLilyPad;
import cn.nukkit.level.generator.populator.tree.SwampTreePopulator;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
//porktodo: this should be flat in most places, and only rise up in a few
public class SwamplandMBiome extends SwampBiome {

    public SwamplandMBiome() {
        super();

        this.setElevation(62, 75);
    }

    @Override
    public String getName() {
        return "Swampland M";
    }
}
