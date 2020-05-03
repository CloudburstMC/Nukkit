package cn.nukkit.level.generator.standard.generation.density;

import cn.nukkit.level.generator.standard.biome.map.BiomeMap;
import cn.nukkit.level.generator.standard.misc.GenerationPass;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.NonNull;
import net.daporkchop.lib.common.util.PValidation;

/**
 * Provides density values used for the initial world surface generation.
 *
 * @author DaPorkchop_
 * @see net.daporkchop.lib.noise.NoiseSource
 */
@JsonDeserialize(using = DensitySourceDeserializer.class)
public interface DensitySource extends GenerationPass {
    /**
     * Gets the density at the given coordinates.
     *
     * @param biomes a {@link BiomeMap} containing the biomes to be used in the world
     * @param x      the X coordinate
     * @param y      the Y coordinate
     * @param z      the Z coordinate
     * @return the density at the given coordinates
     */
    double get(@NonNull BiomeMap biomes, int x, int y, int z);

    /**
     * Gets the density in the given region.
     * <p>
     * Values will be stored in the returned array in XZY order.
     *
     * @param arr        a {@code double[]} to store the density values in. if too small or {@code null}, a new one will be created
     * @param startIndex the index to begin storing values into the array at
     * @param biomes     a {@link BiomeMap} containing the biomes to be used in the world
     * @param x          the X coordinate
     * @param y          the Y coordinate
     * @param z          the Z coordinate
     * @param sizeX      the number of samples to take along the X axis
     * @param sizeY      the number of samples to take along the Y axis
     * @param sizeZ      the number of samples to take along the Z axis
     * @param stepX      the spacing between samples along the X axis
     * @param stepY      the spacing between samples along the Y axis
     * @param stepZ      the spacing between samples along the Z axis
     * @return the density at the given coordinates
     */
    default double[] get(double[] arr, int startIndex, @NonNull BiomeMap biomes, int x, int y, int z, int sizeX, int sizeY, int sizeZ, int stepX, int stepY, int stepZ) {
        int totalSize = PValidation.ensurePositive(sizeX) * PValidation.ensurePositive(sizeY) * PValidation.ensurePositive(sizeZ) + PValidation.ensureNonNegative(startIndex);
        if (arr == null || arr.length < totalSize) {
            double[] newArr = new double[totalSize];
            if (arr != null && startIndex != 0) {
                //copy existing elements into new array
                System.arraycopy(arr, 0, newArr, 0, startIndex);
            }
            arr = newArr;
        }

        for (int i = startIndex, dx = 0; dx < sizeX; dx++) {
            for (int dz = 0; dz < sizeZ; dz++) {
                for (int dy = 0; dy < sizeY; dy++) {
                    arr[i++] = this.get(biomes, x + dx * stepX, y + dy * stepY, z + dz * stepZ);
                }
            }
        }
        return arr;
    }

    @Override
    Identifier getId();
}
