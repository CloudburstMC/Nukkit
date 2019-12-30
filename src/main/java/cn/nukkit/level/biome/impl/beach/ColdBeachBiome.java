package cn.nukkit.level.biome.impl.beach;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.level.biome.type.SandyBiome;
import cn.nukkit.level.generator.populator.impl.WaterIcePopulator;

public class ColdBeachBiome extends SandyBiome {
    private static final Block SNOW_LAYER = Block.get(BlockIds.SNOW_LAYER);

    public ColdBeachBiome() {
        WaterIcePopulator ice = new WaterIcePopulator();
        this.addPopulator(ice);

        this.setBaseHeight(0f);
        this.setHeightVariation(0.025f);
    }

    @Override
    public Block getCover(int x, int z) {
        return SNOW_LAYER;
    }

    @Override
    public String getName() {
        return "Cold Beach";
    }

    @Override
    public boolean isFreezing() {
        return true;
    }
}
