package cn.nukkit.level.generator.biome;

import cn.nukkit.block.Block;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class GrassyBiome extends NormalBiome {
    public GrassyBiome() {
        this.setGroundCover(new Block[]{
                Block.get(Block.GRASS, 0),
                Block.get(Block.DIRT, 0),
                Block.get(Block.DIRT, 0),
                Block.get(Block.DIRT, 0),
                Block.get(Block.DIRT, 0)
        });
    }
}
