package cn.nukkit.server.level.generator.biome;

import cn.nukkit.server.block.Block;
import cn.nukkit.server.block.BlockDirt;
import cn.nukkit.server.block.BlockGrass;
import cn.nukkit.server.block.BlockSnowLayer;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class SnowyBiome extends NormalBiome {
    public SnowyBiome() {
        this.setGroundCover(new Block[]{
                new BlockSnowLayer(),
                new BlockGrass(),
                new BlockDirt(),
                new BlockDirt(),
                new BlockDirt()
        });
    }
}
