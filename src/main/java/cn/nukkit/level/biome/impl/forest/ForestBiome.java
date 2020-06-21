package cn.nukkit.level.biome.impl.forest;

import cn.nukkit.block.BlockSapling;
import cn.nukkit.level.biome.type.GrassyBiome;
import cn.nukkit.level.generator.populator.impl.PopulatorTree;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ForestBiome extends GrassyBiome {

    public static final int TYPE_NORMAL = 0;

    public static final int TYPE_BIRCH = 1;

    public static final int TYPE_BIRCH_TALL = 2;

    public final int type;

    public ForestBiome() {
        this(ForestBiome.TYPE_NORMAL);
    }

    public ForestBiome(final int type) {
        super();

        this.type = type;

        PopulatorTree trees = new PopulatorTree(type == ForestBiome.TYPE_BIRCH_TALL ? BlockSapling.BIRCH_TALL : BlockSapling.BIRCH);
        trees.setBaseAmount(type == ForestBiome.TYPE_NORMAL ? 3 : 6);
        this.addPopulator(trees);

        if (type == ForestBiome.TYPE_NORMAL) {
            //normal forest biomes have both oak and birch trees
            trees = new PopulatorTree(BlockSapling.OAK);
            trees.setBaseAmount(3);
            this.addPopulator(trees);
        }
    }

    @Override
    public String getName() {
        switch (this.type) {
            case ForestBiome.TYPE_BIRCH:
                return "Birch Forest";
            case ForestBiome.TYPE_BIRCH_TALL:
                return "Birch Forest M";
            default:
                return "Forest";
        }
    }

}
