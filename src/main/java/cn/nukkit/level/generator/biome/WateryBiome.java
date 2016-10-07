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
    }

    @Override
    public int getSurfaceBlock() {
        return Block.DIRT;
    }

    @Override
    public int getGroundBlock() {
        return Block.DIRT;
    }

    @Override
    public int getStoneBlock() {
        return Block.STONE;
    }
}
