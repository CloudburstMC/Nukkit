package cn.nukkit.level.generator.standard.biome.map;

import cn.nukkit.level.generator.standard.biome.GenerationBiome;
import cn.nukkit.level.generator.standard.misc.GenerationPass;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Set;

/**
 * A map of the biomes in a given area.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize(using = BiomeMapDeserializer.class)
public interface BiomeMap extends GenerationPass {
    /**
     * Gets the {@link GenerationBiome} at the given world coordinates.
     *
     * @param x the X coordinate
     * @param z the Z coordinate
     * @return the {@link GenerationBiome} at the given coordinates
     */
    GenerationBiome get(int x, int z);

    /**
     * Gets the {@link GenerationBiome}s in the given world region.
     *
     * @param arr   the {@code GenerationBiome[]} to fill. A new array will be created if this array is {@code null} or too small
     * @param x     the region's base X coordinate
     * @param z     the region's base Z coordinate
     * @param sizeX the size of the region along the X axis
     * @param sizeZ the size of the region along the Z axis
     *              Gets the {@link GenerationBiome}s in the given world region.
     */
    GenerationBiome[] getRegion(GenerationBiome[] arr, int x, int z, int sizeX, int sizeZ);

    /**
     * Gets the {@link Identifier}s of the biomes in the given world region.
     *
     * @param arr   the {@code Identifier[]} to fill. A new array will be created if this array is {@code null} or too small
     * @param x     the region's base X coordinate
     * @param z     the region's base Z coordinate
     * @param sizeX the size of the region along the X axis
     * @param sizeZ the size of the region along the Z axis
     *              Gets the {@link Identifier}s in the given world region.
     */
    Identifier[] getRegionIds(Identifier[] arr, int x, int z, int sizeX, int sizeZ);

    /**
     * @return whether or not this {@link BiomeMap}'s results should be cached for performance
     */
    boolean needsCaching();

    /**
     * @return a {@link Set} containing all of the biomes that could be returned by this {@link BiomeMap}
     */
    Set<GenerationBiome> possibleBiomes();

    @Override
    Identifier getId();
}
