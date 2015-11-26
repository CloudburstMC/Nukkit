package cn.nukkit.level.generator.normal.biome;

import cn.nukkit.level.generator.populator.PopulatorTallGrass;
import cn.nukkit.level.generator.populator.PopulatorTree;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class MountainsBiome extends GrassyBiome {

    public MountainsBiome() {
        super();

        PopulatorTree tree = new PopulatorTree();
        tree.setBaseAmount(1);
        this.addPopulator(tree);

        PopulatorTallGrass tallGrass = new PopulatorTallGrass();
        tallGrass.setBaseAmount(1);
        this.addPopulator(tallGrass);

        this.setElevation(63, 127);

        this.temperature = 0.4;
        this.rainfall = 0.5;
    }

    @Override
    public String getName() {
        return "Mountains";
    }
}
