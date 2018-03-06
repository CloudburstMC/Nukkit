package cn.nukkit.level.generator.biome;

import cn.nukkit.level.generator.populator.PopulatorTree;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ExtremeHillsBiome extends GrassyBiome {

    public ExtremeHillsBiome() {
        super();

        PopulatorTree tree = new PopulatorTree();
        tree.setBaseAmount(1);
        this.addPopulator(tree);

        this.setElevation(67, 127);

        this.temperature = 0.4;
        this.rainfall = 0.5;
    }

    @Override
    public String getName() {
        return "Extreme Hills";
    }
}
