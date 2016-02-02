package cn.nukkit.level.generator.biome;

import cn.nukkit.block.Block;
import cn.nukkit.block.Sand;
import cn.nukkit.block.Sandstone;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class SandyBiome extends NormalBiome {
    public SandyBiome() {
        this.setGroundCover(new Block[]{
                new Sand(),
                new Sand(),
                new Sandstone(),
                new Sandstone(),
                new Sandstone()
        });
    }
}
