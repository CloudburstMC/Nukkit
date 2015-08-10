package cn.nukkit.level.generator.normal.biome;

import cn.nukkit.block.Block;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class SnowyBiome extends NormalBiome {
    public SnowyBiome() {
        this.setGroundCover(new Block[]{
                Block.get(Block.SNOW_LAYER, 0),
                Block.get(Block.GRASS, 0),
                Block.get(Block.DIRT, 0),
                Block.get(Block.DIRT, 0),
                Block.get(Block.DIRT, 0)
        });
    }
}
