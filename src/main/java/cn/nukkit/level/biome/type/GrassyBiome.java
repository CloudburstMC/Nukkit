package cn.nukkit.level.biome.type;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDoublePlant;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.generator.populator.impl.PopulatorDoublePlant;
import cn.nukkit.level.generator.populator.impl.PopulatorGrass;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class GrassyBiome extends CoveredBiome {
    private static final Block GRASS = Block.get(BlockID.GRASS);
    private static final Block DIRT = Block.get(BlockID.DIRT);

    public GrassyBiome() {
        PopulatorGrass grass = new PopulatorGrass();
        grass.setBaseAmount(30);
        this.addPopulator(grass);

        PopulatorDoublePlant tallGrass = new PopulatorDoublePlant(BlockDoublePlant.TALL_GRASS);
        tallGrass.setBaseAmount(5);
        this.addPopulator(tallGrass);
    }

    @Override
    public Block getSurface(int x, int y, int z) {
        return GRASS;
    }

    @Override
    public Block getGround(int x, int y, int z) {
        return DIRT;
    }
}
