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
     * <p>
     * [x * zWidth + z]
     */
    public abstract Biome[] getBiomes(int x, int z, int xWidth, int zWidth, @NonNull Biome[] biomes);

    /**
     * Gets a biome array from a {@link ThreadLocal} and generates biome data into it
     *
     * @param x      the x coordinate to start at
     * @param z      the z coordinate to start at
     * @param xWidth the number of biomes to generate on the x axis
     * @param zWidth the number of biomes to generate on the z axis
     * @param tl     the {@link ThreadLocal} containing the biome data array
     * @return the biome array that the biome data was written to
     */
    public Biome[] getBiomes(int x, int z, int xWidth, int zWidth, @NonNull ThreadLocal<Biome[]> tl) {
        Biome[] biomes = tl.get();
        if (biomes == null) {
            tl.set(biomes = new Biome[xWidth * zWidth]);
        }
        return this.getBiomes(x, z, xWidth, zWidth, biomes);
    }
}
