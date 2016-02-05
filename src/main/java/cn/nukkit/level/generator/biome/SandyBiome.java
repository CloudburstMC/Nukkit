package cn.nukkit.level.generator.biome;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockSand;
import cn.nukkit.block.BlockSandstone;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class SandyBiome extends NormalBiome {
    public SandyBiome() {
        this.setGroundCover(new Block[]{
                new BlockSand(),
                new BlockSand(),
                new BlockSandstone(),
                new BlockSandstone(),
                new BlockSandstone()
        });
    }
}
