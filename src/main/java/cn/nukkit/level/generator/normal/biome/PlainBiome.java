package cn.nukkit.level.generator.normal.biome;

import cn.nukkit.level.generator.populator.TallGrass;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PlainBiome extends GrassyBiome {

    public PlainBiome() {
        super();

        TallGrass tallGrass = new TallGrass();
        tallGrass.setBaseAmount(12);

        this.addPopulator(tallGrass);

        this.setElevation(63, 74);

        this.temerature = 0.8;
        this.rainfall = 0.4;
    }

    @Override
    public String getName() {
        return "Plains";
    }
}
