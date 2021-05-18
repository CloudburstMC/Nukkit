package cn.nukkit.world.biome.impl.taiga;

import cn.nukkit.block.BlockSapling;
import cn.nukkit.world.biome.type.GrassyBiome;
import cn.nukkit.world.generator.populator.impl.PopulatorTree;

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

        this.setBaseHeight(0.2f);
        this.setHeightVariation(0.2f);
    }

    @Override
    public String getName() {
        return "Taiga";
    }
}
