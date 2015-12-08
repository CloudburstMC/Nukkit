package cn.nukkit.level.generator.biome;

import cn.nukkit.block.Sapling;
import cn.nukkit.level.generator.populator.PopulatorGrass;
import cn.nukkit.level.generator.populator.PopulatorTallGrass;
import cn.nukkit.level.generator.populator.PopulatorTree;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ForestBiome extends GrassyBiome {
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_BIRCH = 0;

    public int type;

    public ForestBiome() {
        this(TYPE_NORMAL);
    }

    public ForestBiome(int type) {
        super();

        this.type = type;

        PopulatorTree trees = new PopulatorTree(type == TYPE_BIRCH ? Sapling.BIRCH : Sapling.OAK);
        trees.setBaseAmount(5);
        this.addPopulator(trees);

        PopulatorGrass grass = new PopulatorGrass();
        grass.setBaseAmount(30);
        this.addPopulator(grass);

        PopulatorTallGrass tallGrass = new PopulatorTallGrass();
        tallGrass.setBaseAmount(3);

        this.addPopulator(tallGrass);

        this.setElevation(63, 81);

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
