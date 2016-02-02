package cn.nukkit.level.generator.biome;

import cn.nukkit.block.Block;
import cn.nukkit.block.Dirt;
import cn.nukkit.block.Grass;
import cn.nukkit.block.SnowLayer;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class SnowyBiome extends NormalBiome {
    public SnowyBiome() {
        this.setGroundCover(new Block[]{
                new SnowLayer(),
                new Grass(),
                new Dirt(),
                new Dirt(),
                new Dirt()
        });
    }
}
