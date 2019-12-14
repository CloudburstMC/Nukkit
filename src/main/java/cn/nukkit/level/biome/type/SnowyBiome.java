package cn.nukkit.level.biome.type;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.generator.populator.impl.WaterIcePopulator;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public abstract class SnowyBiome extends GrassyBiome {
    private static final Block SNOW_LAYER = Block.get(BlockID.SNOW_LAYER);
    public SnowyBiome() {
        super();

        WaterIcePopulator waterIce = new WaterIcePopulator();
        this.addPopulator(waterIce);
    }

    @Override
    public Block getCover(int x, int z) {
        return SNOW_LAYER;
    }
}
