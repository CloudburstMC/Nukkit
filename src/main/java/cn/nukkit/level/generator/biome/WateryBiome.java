package cn.nukkit.level.generator.biome;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDirt;

/**
 * author: Angelic47
 * Nukkit Project
 */
public abstract class WateryBiome extends NormalBiome {
    public WateryBiome() {
        this.setGroundCover(new Block[]{
                new BlockDirt(),
                new BlockDirt(),
                new BlockDirt(),
                new BlockDirt(),
                new BlockDirt()
        });

        this.surfaceBlock = Block.DIRT;
        this.groundBlock = Block.DIRT;
        this.stoneBlock = Block.STONE;
    }
}
