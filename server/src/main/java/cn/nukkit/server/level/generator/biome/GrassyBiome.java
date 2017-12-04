package cn.nukkit.server.level.generator.biome;

import cn.nukkit.server.block.Block;
import cn.nukkit.server.block.BlockDirt;
import cn.nukkit.server.block.BlockGrass;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class GrassyBiome extends NormalBiome implements CaveBiome {
    public GrassyBiome() {
        this.setGroundCover(new Block[]{
                new BlockGrass(),
                new BlockDirt(),
                new BlockDirt(),
                new BlockDirt(),
                new BlockDirt()
        });
    }

    @Override
    public int getSurfaceBlock() {
        return Block.GRASS;
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
