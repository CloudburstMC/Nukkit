package cn.nukkit.level.biome.impl.taiga;

import cn.nukkit.block.BlockSapling;
import cn.nukkit.level.biome.type.GrassyBiome;
import cn.nukkit.level.generator.populator.PopulatorTree;
import cn.nukkit.level.generator.populator.tree.SpruceBigTreePopulator;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class MegaTaigaBiome extends TaigaBiome {
    public MegaTaigaBiome() {
        super();

        SpruceBigTreePopulator bigTrees = new SpruceBigTreePopulator();
        bigTrees.setBaseAmount(6);
        this.addPopulator(bigTrees);
    }

    @Override
    public String getName() {
        return "Mega Taiga";
    }
}
