package cn.nukkit.world.biome.impl.plains;

import cn.nukkit.world.biome.type.GrassyBiome;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class PlainsBiome extends GrassyBiome {

    public PlainsBiome() {
        super();

        this.setBaseHeight(0.125f);
        this.setHeightVariation(0.05f);
    }

    @Override
    public String getName() {
        return "Plains";
    }
}
