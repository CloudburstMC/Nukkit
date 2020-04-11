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
import static net.daporkchop.lib.random.impl.FastPRandom.*;

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
    public double get(int x, int y, int z, @NonNull BiomeMap biomes) {
        double xd = x; //only do floating-point conversion once
        double yd = y;
        double zd = z;

        //do all noise computations together to allow JIT to potentally apply some optimizations if all noise sources use the same implementation
        // (also this ensures the noise code is cached for the following invocations)
        double selector = clamp(this.selector.get(xd, yd, zd), 0.0d, 1.0d);
        double low = this.low.get(xd, yd, zd);
        double high = this.high.get(xd, yd, zd);

        double outputNoise = lerp(low, high, selector) + this.islands.get(x, z);

        if (yd > this.maxHeightCutoff) {
            double factor = clamp((yd - this.maxHeightCutoff) * 0.015625d * 8.0d, 0.0d, 1.0d);
            outputNoise = outputNoise * (1.0d - factor) - 0.5d * factor;
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
    protected static class IslandCache extends TerrainDoubleCache {
        protected NoiseSource size;
        protected NoiseSource distortion;
        protected long        seed;

        protected double centerIslandRadius;
        protected long   outerIslandStartRadiusSq;

        protected NoiseGenerator islandSize;
        protected NoiseGenerator islandDistortion;

        @JsonCreator
        public IslandCache(
                @JsonProperty(value = "radius", required = true) int radius,
                @JsonProperty(value = "scale", required = true) int scale,
                @JsonProperty(value = "centerIslandRadius", required = true) double centerIslandRadius,
                @JsonProperty(value = "outerIslandStartRadius", required = true) double outerIslandStartRadius,
                @JsonProperty(value = "islandSize", required = true) NoiseGenerator islandSize,
                @JsonProperty(value = "islandDistortion", required = true) NoiseGenerator islandDistortion) {
            super(radius, scale);

            this.centerIslandRadius = centerIslandRadius;
            outerIslandStartRadius /= scale;
            this.outerIslandStartRadiusSq = floorL(outerIslandStartRadius * outerIslandStartRadius);
            this.islandSize = islandSize;
            this.islandDistortion = islandDistortion;
        }

        protected void init(@NonNull PRandom random) {
            this.size = this.islandSize.create(new FastPRandom(random.nextLong()));
            this.distortion = this.islandDistortion.create(new FastPRandom(random.nextLong()));
            this.islandSize = this.islandDistortion = null;

            this.seed = random.nextLong();
        }

        @Override
        protected double computeValue(int x, int z, int radius, int scale) {
            final double islandRadius = this.centerIslandRadius;
            final long outerIslandStartRadiusSq = this.outerIslandStartRadiusSq;

            double val = islandRadius - sqrt((double) x * (double) x + (double) z * (double) z);

            int tileX = x / scale;
            int tileZ = z / scale;

            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    long islandX = tileX + dx;
                    long islandZ = tileZ + dz;

                    if (islandX * islandX + islandZ * islandZ > outerIslandStartRadiusSq && (mix32(mix64(this.seed + islandX) + islandZ) & 0xFF) == 0)    {
                        double size = this.size.get(islandX, islandZ);
                        double weight = this.distortion.get(x, z);

                        double offsetX = dx * scale - (x % scale);
                        double offsetZ = dz * scale - (z % scale);

                        val = Math.max(val, size - sqrt(offsetX * offsetX + offsetZ * offsetZ) * weight);
                    }
                }
            }

            return clamp(val / islandRadius, -0.78740d, 0.62992d) - 0.0625d;
        }
    }
}
