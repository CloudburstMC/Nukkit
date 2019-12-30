package cn.nukkit.level.biome.impl.ocean;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.level.biome.type.WateryBiome;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class OceanBiome extends WateryBiome {
    private static final Block GRAVEL = Block.get(BlockIds.GRAVEL);

    public OceanBiome() {
        this.setBaseHeight(-1f);
        this.setHeightVariation(0.1f);
    }

    @Override
    public String getName() {
        return "Ocean";
    }

    @Override
    public Block getGround(int x, int y, int z) {
        return GRAVEL;
    }
}
