package cn.nukkit.level.generator.standard.generation.density;

import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.level.generator.standard.biome.BiomeTerrainCache;
import cn.nukkit.level.generator.standard.biome.map.BiomeMap;
import cn.nukkit.level.generator.standard.generation.noise.NoiseGenerator;
import cn.nukkit.level.generator.standard.misc.AbstractGenerationPass;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.NonNull;
import net.daporkchop.lib.common.util.PValidation;
import net.daporkchop.lib.noise.NoiseSource;
import net.daporkchop.lib.random.PRandom;
import net.daporkchop.lib.random.impl.FastPRandom;

import static java.lang.Math.*;
import static java.util.Objects.*;
import static net.daporkchop.lib.math.primitive.PMath.*;

/**
 * A {@link NoiseSource} that provides noise similar to that of vanilla terrain.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public class VanillaDensitySource extends AbstractGenerationPass implements DensitySource {
    public static final Identifier ID = Identifier.fromString("nukkitx:vanilla");

    //these fields aren't sorted in ascending order by size (so there's a possibility that fields might not be word-aligned), however they ARE sorted
    // by the order in which they're used (so they can be prefetched into the cache)
    private NoiseSource selector;
    private NoiseSource low;
    private NoiseSource high;
    private NoiseSource depth;

    @JsonProperty("terrainSmoothing")
    private BiomeTerrainCache terrainCache;

    @JsonProperty
    private double specialHeightVariation = 0.25d;
    @JsonProperty
    private double heightFactor = 1.0d;
    @JsonProperty
    private double heightOffset = 0.0d;
    @JsonProperty
    private double heightVariationFactor = 1.0d;
    @JsonProperty
    private double heightVariationOffset = 0.0d;

    @JsonProperty
    private NoiseGenerator selectorNoise;
    @JsonProperty
    private NoiseGenerator lowNoise;
    @JsonProperty
    private NoiseGenerator highNoise;
    @JsonProperty
    private NoiseGenerator depthNoise;

    @Override
    protected void init0(long levelSeed, long localSeed, StandardGenerator generator) {
        PRandom random = new FastPRandom(localSeed);
        this.selector = requireNonNull(this.selectorNoise, "selectorNoise must be set!").create(new FastPRandom(random.nextLong()));
        this.low = requireNonNull(this.lowNoise, "lowNoise must be set!").create(new FastPRandom(random.nextLong()));
        this.high = requireNonNull(this.highNoise, "highNoise must be set!").create(new FastPRandom(random.nextLong()));
        this.depth = requireNonNull(this.depthNoise, "depthNoise must be set!").create(new FastPRandom(random.nextLong()));

        this.selectorNoise = this.lowNoise = this.highNoise = this.depthNoise = null;

        requireNonNull(this.terrainCache, "terrainSmoothing must be set!");
    }

    @Override
    public double get(@NonNull BiomeMap biomes, int x, int y, int z) {
        //do all noise computations together to allow JIT to potentally apply some optimizations if all noise sources use the same implementation
        // (also this ensures the noise code is cached for the following invocations)
        double selector = clamp(this.selector.get(x, y, (double) z), 0.0d, 1.0d);
        double low = this.low.get(x, y, (double) z);
        double high = this.high.get(x, y, (double) z);

        double outputNoise = lerp(low, high, selector) + this.getDepth(x, z);

        BiomeTerrainCache.Data terrainData = this.terrainCache.get(x, z, biomes);
        double height = terrainData.baseHeight * this.heightFactor + this.heightOffset;
        double variation = terrainData.heightVariation;
        if (height > y) {
            variation *= this.specialHeightVariation;
        }
        variation = variation * this.heightVariationFactor + this.heightVariationOffset;

        outputNoise *= variation;
        outputNoise += height;
        outputNoise -= signum(variation) * y;

        return outputNoise;
    }

    @Override
    public double[] get(double[] arr, int startIndex, @NonNull BiomeMap biomes, int x, int y, int z, int sizeX, int sizeY, int sizeZ, int stepX, int stepY, int stepZ) {
        int totalSize = PValidation.ensurePositive(sizeX) * PValidation.ensurePositive(sizeY) * PValidation.ensurePositive(sizeZ) + PValidation.ensureNonNegative(startIndex);
        if (arr == null || arr.length < totalSize) {
            double[] newArr = new double[totalSize];
            if (arr != null && startIndex != 0) {
                //copy existing elements into new array
                System.arraycopy(arr, 0, newArr, 0, startIndex);
            }
            arr = newArr;
        }

        for (int i = startIndex, dx = 0, xx = x; dx < sizeX; dx++, xx += stepX) {
            for (int dz = 0, zz = z; dz < sizeZ; dz++, zz += stepZ) {
                double depth = this.getDepth(xx, zz);

                BiomeTerrainCache.Data terrainData = this.terrainCache.get(xx, zz, biomes);
                double height = terrainData.baseHeight * this.heightFactor + this.heightOffset;
                double columnVariation = terrainData.heightVariation;

                for (int dy = 0, yy = y; dy < sizeY; dy++, yy += stepY) {
                    double selector = clamp(this.selector.get(xx, yy, (double) zz), 0.0d, 1.0d);
                    double low = this.low.get(xx, yy, (double) zz);
                    double high = this.high.get(xx, yy, (double) zz);

                    double variation = (height > yy ? columnVariation * this.specialHeightVariation : columnVariation) * this.heightVariationFactor + this.heightVariationOffset;
                    arr[i++] = (lerp(low, high, selector) + depth) * variation + height - signum(variation) * yy;
                }
            }
        }
        return arr;
    }

    protected double getDepth(double x, double z) {
        double depth = this.depth.get(x, z);
        if (depth < 0.0d) {
            depth *= -0.3d;
        }
        depth = clamp(depth * 3.0d - 2.0d, -2.0d, 1.0d);
        depth /= depth < 0.0d ? 2.0d * 2.0d * 1.4d : 8.0d;
        depth *= 0.2d * 17.0d / 64.0d;
        return depth;
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}
