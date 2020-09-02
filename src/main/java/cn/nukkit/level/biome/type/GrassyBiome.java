package cn.nukkit.level.biome.type;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDoublePlant;
import cn.nukkit.level.generator.populator.impl.PopulatorDoublePlant;
import cn.nukkit.level.generator.populator.impl.PopulatorGrass;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class GrassyBiome extends CoveredBiome {
    public GrassyBiome() {
        PopulatorGrass grass = new PopulatorGrass();
        grass.setBaseAmount(30);
        this.addPopulator(grass);

        PopulatorDoublePlant tallGrass = new PopulatorDoublePlant(BlockDoublePlant.TALL_GRASS);
        tallGrass.setBaseAmount(5);
        this.addPopulator(tallGrass);
    }

    @Override
    public int getSurfaceId(int x, int y, int z) {
        return GRASS << Block.DATA_BITS;
    }

    @Override
    public int getGroundId(int x, int y, int z) {
        return DIRT << Block.DATA_BITS;
    }
}
