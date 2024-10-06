package cn.nukkit.level.biome.impl.forest;

import cn.nukkit.block.BlockSapling;
import cn.nukkit.level.biome.type.GrassyBiome;
import cn.nukkit.level.generator.populator.impl.PopulatorFallenTree;
import cn.nukkit.level.generator.populator.impl.PopulatorTree;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class ForestBiome extends GrassyBiome {
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_BIRCH = 1;
    public static final int TYPE_BIRCH_TALL = 2;

    public final int type;

    public ForestBiome() {
        this(TYPE_NORMAL);
    }

    public ForestBiome(int type) {
        super();

        this.type = type;

        PopulatorTree trees = new PopulatorTree(type == TYPE_BIRCH_TALL ? BlockSapling.BIRCH_TALL : BlockSapling.BIRCH);
        trees.setBaseAmount(type == TYPE_NORMAL ? 3 : 10);
        trees.setRandomAmount(3);
        this.addPopulator(trees);

        if (type == TYPE_NORMAL) {
            // Normal forest biomes have both oak and birch trees
            trees = new PopulatorTree(BlockSapling.OAK);
            trees.setBaseAmount(4);
            trees.setRandomAmount(3);
            this.addPopulator(trees);
        }

        PopulatorFallenTree fallenTree = new PopulatorFallenTree();
        fallenTree.setType(type);
        this.addPopulator(fallenTree);
    }

    @Override
    public String getName() {
        switch (this.type) {
            case TYPE_BIRCH:
                return "Birch Forest";
            case TYPE_BIRCH_TALL:
                return "Birch Forest M";
            default:
                return "Forest";
        }
    }
}
