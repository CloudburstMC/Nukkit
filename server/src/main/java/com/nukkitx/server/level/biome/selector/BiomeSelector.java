package com.nukkitx.server.level.biome.selector;

import com.nukkitx.api.level.data.Biome;
import lombok.NonNull;

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
    public abstract Biome[] getBiomes(int x, int z, int xWidth, int zWidth, @NonNull Biome[] biomes);
}
