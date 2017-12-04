package cn.nukkit.server.level.generator.biome;

import cn.nukkit.server.block.Block;
import cn.nukkit.server.block.BlockDirt;

/**
 * author: Angelic47
 * Nukkit Project
 */
public abstract class WateryBiome extends NormalBiome implements CaveBiome {
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
