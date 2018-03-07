package cn.nukkit.level.biome.impl;

import cn.nukkit.level.biome.type.GrassyBiome;
import cn.nukkit.level.generator.populator.tree.JungleBigTreePopulator;
import cn.nukkit.level.generator.populator.tree.JungleTreePopulator;

public class JungleBiome extends GrassyBiome {

    public JungleBiome() {
        super();
        JungleTreePopulator trees = new JungleTreePopulator();
        JungleBigTreePopulator bigTrees = new JungleBigTreePopulator();
        trees.setBaseAmount(10);
        bigTrees.setBaseAmount(6);
        //PopulatorTallGrass tallGrass = new PopulatorTallGrass();

        //PopulatorFern fern = new PopulatorFern();
        //fern.setBaseAmount(30);

        //this.addPopulator(fern);
        this.addPopulator(bigTrees);
        this.addPopulator(trees);
        this.setElevation(67, 73);
        this.temperature = 1.2f;
        this.rainfall = 0.9f;
    }

    @Override
    public String getName() {
        return "Jungle";
    }
}
