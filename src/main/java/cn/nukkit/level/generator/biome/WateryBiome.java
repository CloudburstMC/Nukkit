package cn.nukkit.level.generator.biome;

import cn.nukkit.block.Block;

/**
 * author: Angelic47
 * Nukkit Project
 */
public abstract class WateryBiome extends NormalBiome {
    public WateryBiome() {
        this.setGroundCover(new Block[]{
                Block.get(Block.DIRT, 0),
                Block.get(Block.DIRT, 0),
                Block.get(Block.DIRT, 0),
                Block.get(Block.DIRT, 0),
                Block.get(Block.DIRT, 0)
        });
    }
}
