package cn.nukkit.level.biome.impl.taiga;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.generator.populator.impl.WaterIcePopulator;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class ColdTaigaBiome extends TaigaBiome {
    private static final Block SNOW_LAYER = Block.get(BlockID.SNOW_LAYER);

    public ColdTaigaBiome() {
        super();

        WaterIcePopulator ice = new WaterIcePopulator();
        this.addPopulator(ice);

        this.setBaseHeight(0.2f);
        this.setHeightVariation(0.2f);
    }

    @Override
    public String getName() {
        return "Cold Taiga";
    }

    @Override
    public Block getCover(int x, int z) {
        return SNOW_LAYER;
    }

    @Override
    public boolean isFreezing() {
        return true;
    }
}
