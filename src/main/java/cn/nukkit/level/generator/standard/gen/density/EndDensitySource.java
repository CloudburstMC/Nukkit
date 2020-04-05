package cn.nukkit.level.generator.standard.gen.density;

import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.level.generator.standard.biome.BiomeTerrainCache;
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
 * A {@link NoiseSource} that provides noise similar to that of vanilla terrain.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public class EndDensitySource extends AbstractGenerationPass implements DensitySource {
    public static final Identifier ID = Identifier.fromString("nukkitx:end");

    //these fields aren't sorted in ascending order by size (so there's a possibility that fields might not be word-aligned), however they ARE sorted
    // by the order in which they're used (so they can be prefetched into the cache)
    private NoiseSource selector;
    private NoiseSource low;
    private NoiseSource high;

    @JsonProperty("height")
    private HeightCache heightCache;

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
    @JsonProperty
    private NoiseGenerator heightNoise;

    @Override
    protected void init0(long levelSeed, long localSeed, StandardGenerator generator) {
        PRandom random = new FastPRandom(localSeed);
        this.selector = requireNonNull(this.selectorNoise, "selectorNoise must be set!").create(new FastPRandom(random.nextLong()));
        this.low = requireNonNull(this.lowNoise, "lowNoise must be set!").create(new FastPRandom(random.nextLong()));
        this.high = requireNonNull(this.highNoise, "highNoise must be set!").create(new FastPRandom(random.nextLong()));
        requireNonNull(this.heightCache, "height must be set!").height
                = requireNonNull(this.heightNoise, "heightNoise must be set!").create(new FastPRandom(random.nextLong()));

        this.selectorNoise = this.lowNoise = this.highNoise = this.heightNoise = null;
    }

    @Override
    public double get(int x, int y, int z, @NonNull BiomeMap biomes) {
        double xd = x; //only do floating-point conversion once
        double yd = y;
        double zd = z;

        //do all noise computations together to allow JIT to potentally apply some optimizations if all noise sources use the same implementation
        // (also this ensures the noise code is cached for the following invocations)
        double selector = clamp(this.selector.get(xd, yd, zd), 0.0d, 1.0d);
        double low = this.low.get(xd, yd, zd);
        double high = this.high.get(xd, yd, zd);

        double outputNoise = lerp(low, high, selector) + this.heightCache.get(x, z);
        //double outputNoise = lerp(low, high, selector) + this.heightCache.height.get(x, z);

        if (yd > this.maxHeightCutoff) {
            double factor = clamp(((yd * 0.125d) - (this.maxHeightCutoff * 0.125d)) * 0.015625d, 0.0d, 1.0d);
            outputNoise = outputNoise * (1.0d - factor) - 3000.0d * factor;
        } else if (yd < this.minHeightCutoff)   {
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
    protected static class HeightCache extends TerrainDoubleCache {
        protected NoiseSource height;
        protected double islandRadius;
        protected long   outerIslandStartRadiusSq;
        protected double outerNoiseConsiderThreshold;

        @JsonCreator
        public HeightCache(
                @JsonProperty(value = "radius", required = true) int radius,
                @JsonProperty(value = "scale", required = true) int scale,
                @JsonProperty(value = "islandRadius", required = true) double islandRadius,
                @JsonProperty(value = "outerIslandStartRadius", required = true) double outerIslandStartRadius,
                @JsonProperty(value = "outerNoiseConsiderThreshold", required = true) double outerNoiseConsiderThreshold) {
            super(radius, scale);

            this.islandRadius = islandRadius;
            outerIslandStartRadius /= scale;
            this.outerIslandStartRadiusSq = floorL(outerIslandStartRadius * outerIslandStartRadius);
            this.outerNoiseConsiderThreshold = outerNoiseConsiderThreshold;
        }

        @Override
        protected double computeValue(int x, int z, int radius, int scale) {
            final NoiseSource height = this.height;
            final double islandRadius = this.islandRadius;
            final long outerIslandStartRadiusSq = this.outerIslandStartRadiusSq;
            final double outerNoiseConsiderThreshold = this.outerNoiseConsiderThreshold;

            double val = clamp(islandRadius - sqrt((double) x * (double) x + (double) z * (double) z) * 8.0d, -100.0d, 80.0d);

            int baseX = x / scale;
            int baseZ = z / scale;

            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    long offsetX = baseX + dx;
                    long offsetZ = baseZ + dz;

                    if (offsetX * offsetX + offsetZ * offsetZ <= outerIslandStartRadiusSq
                            || height.get(offsetX, offsetZ) >= outerNoiseConsiderThreshold) {
                        continue;
                    }

                    double weight = (abs(offsetX) * 3439.0d + abs(offsetZ) * 147.0d) % 13.0d + 9.0d;
                    double tileX = dx * scale - (x % scale);
                    double tileZ = dz * scale - (z % scale);

                    val = Math.max(val, clamp(islandRadius - sqrt(tileX * tileX + tileZ * tileZ) * weight, -100.0d, 80.0d));
                }
            }

            return val - 8.0d;
        }
    }
}
