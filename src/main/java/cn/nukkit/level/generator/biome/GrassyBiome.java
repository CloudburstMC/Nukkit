package cn.nukkit.level.generator.biome;

import cn.nukkit.block.Block;
import cn.nukkit.block.Dirt;
import cn.nukkit.block.Grass;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class GrassyBiome extends NormalBiome {
    public GrassyBiome() {
        this.setGroundCover(new Block[]{
                new Grass(),
                new Dirt(),
                new Dirt(),
                new Dirt(),
                new Dirt()
        });
    }
}
