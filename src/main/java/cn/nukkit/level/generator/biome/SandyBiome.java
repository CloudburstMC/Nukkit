package cn.nukkit.level.generator.biome;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockSand;
import cn.nukkit.block.BlockSandstone;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class SandyBiome extends NormalBiome implements CaveBiome {
    public SandyBiome() {
        this.setGroundCover(new Block[]{
                new BlockSand(),
                new BlockSand(),
                new BlockSand(),
                new BlockSandstone(),
                new BlockSandstone()
        });
    }

    @Override
    public int getSurfaceBlock() {
        return Block.SAND;
    }

    @Override
    public int getGroundBlock() {
        return Block.SAND;
    }

    @Override
    public int getStoneBlock() {
        return Block.SANDSTONE;
    }
}
