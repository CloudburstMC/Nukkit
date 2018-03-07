package cn.nukkit.level.biome.impl;

import cn.nukkit.block.BlockSapling;
import cn.nukkit.level.biome.type.GrassyBiome;
import cn.nukkit.level.generator.populator.PopulatorTree;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ForestBiome extends GrassyBiome {
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_BIRCH = 1;

    public final int type;

    public ForestBiome() {
        this(TYPE_NORMAL);
    }

    public ForestBiome(int type) {
        super();

        this.type = type;

        PopulatorTree trees = new PopulatorTree(type == TYPE_BIRCH ? BlockSapling.BIRCH : BlockSapling.OAK);
        trees.setBaseAmount(5);
        this.addPopulator(trees);

        this.setElevation(67, 70);

        if (type == TYPE_BIRCH) {
            this.temperature = 0.5;
            this.rainfall = 0.5;
        } else {
            this.temperature = 0.7;
            this.temperature = 0.8;
        }
    }

    @Override
    public String getName() {
        return this.type == TYPE_BIRCH ? "Birch Forest" : "Forest";
    }
}
