package cn.nukkit.level.generator.biome;

import cn.nukkit.block.Block;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class SandyBiome extends NormalBiome {
    public SandyBiome() {
        this.setGroundCover(new Block[]{
                Block.get(Block.SAND, 0),
                Block.get(Block.SAND, 0),
                Block.get(Block.SANDSTONE, 0),
                Block.get(Block.SANDSTONE, 0),
                Block.get(Block.SANDSTONE, 0)
        });
    }
}
