package cn.nukkit.level.generator.standard.gen.density;

import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.level.generator.standard.biome.BiomeTerrainCache;
import cn.nukkit.level.generator.standard.biome.map.BiomeMap;
import cn.nukkit.level.generator.standard.gen.noise.NoiseGenerator;
import cn.nukkit.level.generator.standard.misc.AbstractGenerationPass;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
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
public class VanillaDensitySource extends AbstractGenerationPass implements DensitySource {
    private static final double DEPTH_SCALE_FACTOR_1 = -0.3d;
    private static final double DEPTH_SCALE_FACTOR_2 = 0.17857142857142d;
    private static final double DEPTH_SCALE_FACTOR_3 = 0.125d;
    private static final double DEPTH_SCALE_FACTOR_4 = 0.053125d;

    public static final Identifier ID = Identifier.fromString("nukkitx:vanilla");

    //these fields aren't sorted in ascending order by size (so there's a possibility that fields might not be word-aligned), however they ARE sorted
    // by the order in which they're used (so they can be prefetched into the cache)
    private NoiseSource selector;
    private NoiseSource low;
    private NoiseSource high;
    private NoiseSource depth;

    @Getter
    private final BiomeTerrainCache terrainCache = new BiomeTerrainCache(2);

    @JsonProperty
    private double specialHeightVariation = 0.25d;
    @JsonProperty
    private double heightFactor           = 1.0d;
    @JsonProperty
    private double heightOffset           = 0.0d;
    @JsonProperty
    private double heightVariationFactor  = 1.0d;
    @JsonProperty
    private double heightVariationOffset  = 0.0d;

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

        double outputNoise = lerp(low, high, selector);

        double depth = this.depth.get(xd, zd);

        if (depth < 0.0d) {
            depth *= DEPTH_SCALE_FACTOR_1;
        }
        depth = clamp(depth * 3.0d - 2.0d, -2.0d, 1.0d);
        depth *= depth < 0.0d ? DEPTH_SCALE_FACTOR_2 : DEPTH_SCALE_FACTOR_3;
        depth *= DEPTH_SCALE_FACTOR_4;

        outputNoise += depth;

        BiomeTerrainCache.Data terrainData = this.terrainCache.get(x, z, biomes);
        double height = terrainData.baseHeight * this.heightFactor + this.heightOffset;
        double volatility = terrainData.heightVariation;
        if (height > yd) {
            volatility *= this.specialHeightVariation;
        }
        volatility = volatility * this.heightVariationFactor + this.heightVariationOffset;

        outputNoise *= volatility;
        outputNoise += height;
        outputNoise -= signum(volatility) * yd;

        return outputNoise;
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}
