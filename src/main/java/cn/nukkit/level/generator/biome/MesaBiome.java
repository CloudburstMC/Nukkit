package cn.nukkit.level.generator.biome;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockRedSandstone;
import cn.nukkit.block.BlockSand;
import cn.nukkit.block.BlockSandstone;

public class MesaBiome extends SandyBiome {
    public MesaBiome() {
        this.setGroundCover(new Block[]{
                new BlockSand(BlockSand.RED),
                new BlockSand(BlockSand.RED),
                new BlockSand(BlockSand.RED),
                new BlockRedSandstone(),
                new BlockRedSandstone()
        });
    }

    @Override
    public String getName() {
        return "Mesa";
    }
}
