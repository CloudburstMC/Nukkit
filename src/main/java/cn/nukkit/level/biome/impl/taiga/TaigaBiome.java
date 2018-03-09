package cn.nukkit.level.biome.impl.taiga;

import cn.nukkit.block.BlockSapling;
import cn.nukkit.level.biome.type.GrassyBiome;
import cn.nukkit.level.generator.populator.impl.PopulatorTree;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class TaigaBiome extends GrassyBiome {
    public TaigaBiome() {
        super();

        PopulatorTree trees = new PopulatorTree(BlockSapling.SPRUCE);
        trees.setBaseAmount(10);
        this.addPopulator(trees);

        this.setElevation(67, 70);
    }

    @Override
    public String getName() {
        return "Taiga";
    }
}
