package cn.nukkit.level.biome.type;

import cn.nukkit.block.BlockDoublePlant;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.generator.populator.impl.PopulatorDoublePlant;
import cn.nukkit.level.generator.populator.impl.PopulatorGrass;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class GrassyBiome extends CoveredBiome {

    public GrassyBiome() {
        final PopulatorGrass grass = new PopulatorGrass();
        grass.setBaseAmount(30);
        this.addPopulator(grass);

        final PopulatorDoublePlant tallGrass = new PopulatorDoublePlant(BlockDoublePlant.TALL_GRASS);
        tallGrass.setBaseAmount(5);
        this.addPopulator(tallGrass);
    }

    @Override
    public int getSurfaceBlock(final int y) {
        return BlockID.GRASS;
    }

    @Override
    public int getGroundBlock(final int y) {
        return BlockID.DIRT;
    }

}
