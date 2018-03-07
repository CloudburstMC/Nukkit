package cn.nukkit.level.generator.biome.type;

import cn.nukkit.level.generator.populator.PopulatorTallGrass;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class SnowyBiome extends GrassyBiome {
    public SnowyBiome() {
        PopulatorTallGrass tallGrass = new PopulatorTallGrass();
        tallGrass.setBaseAmount(5);
        this.addPopulator(tallGrass);
    }

    @Override
    public int getCoverBlock() {
        return SNOW_LAYER;
    }
}
