package cn.nukkit.level.generator.biome;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDirt;
import cn.nukkit.block.BlockGrass;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class GrassyBiome extends NormalBiome {
    public GrassyBiome() {
        this.setGroundCover(new Block[]{
                new BlockGrass(),
                new BlockDirt(),
                new BlockDirt(),
                new BlockDirt(),
                new BlockDirt()
        });

        this.surfaceBlock = Block.GRASS;
        this.groundBlock = Block.DIRT;
        this.stoneBlock = Block.STONE;
    }
}