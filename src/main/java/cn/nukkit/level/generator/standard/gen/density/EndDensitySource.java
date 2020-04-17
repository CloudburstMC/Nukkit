package cn.nukkit.level.generator.standard.gen.density;

import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.level.generator.standard.biome.map.BiomeMap;
import cn.nukkit.level.generator.standard.gen.noise.NoiseGenerator;
import cn.nukkit.level.generator.standard.misc.AbstractGenerationPass;
import cn.nukkit.level.generator.standard.misc.TerrainDoubleCache;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.NonNull;
import net.daporkchop.lib.noise.NoiseSource;
import net.daporkchop.lib.random.PRandom;
import net.daporkchop.lib.random.impl.FastPRandom;

import static java.lang.Math.*;
import static java.util.Objects.*;
import static net.daporkchop.lib.math.primitive.PMath.*;

/**
 * A {@link NoiseSource} that provides noise similar to that of vanilla's end terrain.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public class EndDensitySource extends AbstractGenerationPass implements DensitySource {
    public static final Identifier ID = Identifier.fromString("nukkitx:end");

    private static final double NOISE_SCALE_FACTOR = ((1 << 16) - 1.0d) / 512.0d;

    //these fields aren't sorted in ascending order by size (so there's a possibility that fields might not be word-aligned), however they ARE sorted
    // by the order in which they're used (so they can be prefetched into the cache)
    private NoiseSource selector;
    private NoiseSource low;
    private NoiseSource high;

    @JsonProperty
    private IslandCache islands;

    @JsonProperty
    private double maxHeightCutoff = 112.0d;
    @JsonProperty
    private double minHeightCutoff = 64.0d;

    @JsonProperty
    private NoiseGenerator selectorNoise;
    @JsonProperty
    private NoiseGenerator lowNoise;
    @JsonProperty
    private NoiseGenerator highNoise;

    @Override
    protected void init0(long levelSeed, long localSeed, StandardGenerator generator) {
        PRandom random = new FastPRandom(localSeed);
        this.selector = requireNonNull(this.selectorNoise, "selectorNoise must be set!").create(new FastPRandom(random.nextLong()));
        this.low = requireNonNull(this.lowNoise, "lowNoise must be set!").create(new FastPRandom(random.nextLong()));
        this.high = requireNonNull(this.highNoise, "highNoise must be set!").create(new FastPRandom(random.nextLong()));
        this.selectorNoise = this.lowNoise = this.highNoise = null;

        requireNonNull(this.islands, "islands must be set!").init(random);
    }

    @Override
    public double get(@NonNull BiomeMap biomes, int x, int y, int z) {
        double xd = x; //only do floating-point conversion once
        double yd = y;
        double zd = z;

        //do all noise computations together to allow JIT to potentally apply some optimizations if all noise sources use the same implementation
        // (also this ensures the noise code is cached for the following invocations)
        double selector = clamp(this.selector.get(xd, yd, zd), 0.0d, 1.0d);
        double low = this.low.get(xd, yd, zd) * NOISE_SCALE_FACTOR;
        double high = this.high.get(xd, yd, zd) * NOISE_SCALE_FACTOR;

        double outputNoise = lerp(low, high, selector) - 8.0d + this.islands.get(x, z);

        if (yd > this.maxHeightCutoff) {
            double factor = clamp(((yd * 0.125d) - (this.minHeightCutoff * 0.125d)) * 0.015625d, 0.0d, 1.0d);
            outputNoise = outputNoise * (1.0d - factor) - 3000.0d * factor;
        } else if (yd < this.minHeightCutoff) {
            double factor = (((this.minHeightCutoff * 0.125d) - (yd * 0.125d)) / ((this.minHeightCutoff * 0.125d) - 1.0d));
            outputNoise = outputNoise * (1.0d - factor) - 30.0d * factor;
        }

        return outputNoise;
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @JsonDeserialize
    protected static class IslandCache extends TerrainDoubleCache {
        protected NoiseSource island;
        protected NoiseSource weight;

        protected double islandRadius;
        protected double outerIslandStartRadiusSq;
        protected double outerIslandSeedThreshold;

        protected NoiseGenerator islandNoise;
        protected NoiseGenerator islandWeight;

        @JsonCreator
        public IslandCache(
                @JsonProperty(value = "radius", required = true) int radius,
                @JsonProperty(value = "islandRadius", required = true) double islandRadius,
                @JsonProperty(value = "outerIslandStartRadius", required = true) double outerIslandStartRadius,
                @JsonProperty(value = "outerIslandSeedThreshold", required = true) double outerIslandSeedThreshold,
                @JsonProperty(value = "islandNoise", required = true) NoiseGenerator islandNoise,
                @JsonProperty(value = "islandWeight", required = true) NoiseGenerator islandWeight) {
            super(radius, 0);

            this.islandRadius = islandRadius;
            this.outerIslandStartRadiusSq = (outerIslandStartRadius / 16.0d) * (outerIslandStartRadius / 16.0d);
            this.outerIslandSeedThreshold = outerIslandSeedThreshold;
            this.islandNoise = islandNoise;
            this.islandWeight = islandWeight;
        }

        protected void init(@NonNull PRandom random) {
            this.island = this.islandNoise.create(new FastPRandom(random.nextLong()));
            this.weight = this.islandWeight.create(new FastPRandom(random.nextLong()));
            this.islandNoise = this.islandWeight = null;
        }

        @Override
        protected double computeValue(int x, int z, int radius, int scale) {
            final double islandRadius = this.islandRadius;
            final double outerIslandStartRadiusSq = this.outerIslandStartRadiusSq;
            final double outerIslandSeedThreshold = this.outerIslandSeedThreshold;

            final double chunkX = x >> 4;
            final double chunkZ = z >> 4;
            final double tileX = (x & 0xF) * 0.125d;
            final double tileZ = (z & 0xF) * 0.125d;

            double val = islandRadius - sqrt((x * 0.125d) * (x * 0.125d) + (z * 0.125d) * (z * 0.125d)) * 8.0d;

            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    double islandX = chunkX + dx;
                    double islandZ = chunkZ + dz;

                    if (islandX * islandX + islandZ * islandZ > outerIslandStartRadiusSq && this.island.get(islandX, islandZ) < outerIslandSeedThreshold) {
                        double weight = this.weight.get(islandX, islandZ);

                        double offsetX = tileX - dx * 2.0d;
                        double offsetZ = tileZ - dz * 2.0d;

                        val = Math.max(val, islandRadius - sqrt(offsetX * offsetX + offsetZ * offsetZ) * weight);
                    }
                }
            }

            return clamp(val, -100.0d, 80.0d);
        }
    }
}
