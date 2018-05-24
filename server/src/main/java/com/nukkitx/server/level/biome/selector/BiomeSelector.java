package com.nukkitx.server.level.biome.selector;

import com.nukkitx.server.level.biome.NukkitBiome;

/**
 * @author DaPorkchop_
 */
public abstract class BiomeSelector {
    public abstract void init(long seed);

    /**
     * indexed as in:
     *
     * [x * xWidth + z * zWidth]
     */
    public abstract NukkitBiome[] getBiomes(int x, int z, int xWidth, int zWidth);
}
