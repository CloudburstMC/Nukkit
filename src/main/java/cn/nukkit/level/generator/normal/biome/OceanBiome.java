package cn.nukkit.level.generator.normal.biome;

import cn.nukkit.level.generator.populator.PopulatorTallGrass;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class OceanBiome extends GrassyBiome {

    public OceanBiome() {
        super();

        PopulatorTallGrass tallGrass = new PopulatorTallGrass();
        tallGrass.setBaseAmount(5);

        this.addPopulator(tallGrass);

        this.setElevation(46, 58);

        this.temperature = 0.5;
        this.rainfall = 0.5;
    }

    @Override
    public String getName() {
        return "Ocean";
    }
}
