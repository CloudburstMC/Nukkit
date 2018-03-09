package cn.nukkit.level.biome.impl.plains;

import cn.nukkit.level.biome.type.GrassyBiome;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class PlainsBiome extends GrassyBiome {

    public PlainsBiome() {
        super();

        this.setElevation(67, 72);

        this.temperature = 0.8;
        this.rainfall = 0.4;
    }

    @Override
    public String getName() {
        return "Plains";
    }
}
