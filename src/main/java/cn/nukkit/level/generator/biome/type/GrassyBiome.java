package cn.nukkit.level.generator.biome.type;

import cn.nukkit.level.generator.populator.PopulatorGrass;
import cn.nukkit.level.generator.populator.PopulatorTallGrass;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class GrassyBiome extends CoveredBiome {
    public GrassyBiome() {
        PopulatorGrass grass = new PopulatorGrass();
        grass.setBaseAmount(30);
        this.addPopulator(grass);

        PopulatorTallGrass tallGrass = new PopulatorTallGrass();
        tallGrass.setBaseAmount(5);
        this.addPopulator(tallGrass);
    }

    @Override
    public int getSurfaceBlock() {
        return GRASS;
    }

    @Override
    public int getGroundBlock() {
        return DIRT;
    }
}
