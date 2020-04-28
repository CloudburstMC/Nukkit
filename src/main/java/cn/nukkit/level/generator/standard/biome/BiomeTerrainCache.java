package cn.nukkit.level.generator.standard.biome;

import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.generator.standard.biome.map.BiomeMap;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.common.ref.Ref;
import net.daporkchop.lib.common.ref.ThreadRef;
import net.daporkchop.lib.common.util.PValidation;

/**
 * @author DaPorkchop_
 */
@JsonDeserialize
public final class BiomeTerrainCache {
    private final Ref<Long2ObjectLinkedOpenHashMap<Data>> cacheCache = ThreadRef.soft(Long2ObjectLinkedOpenHashMap::new);
    private final double[] weights;
    private final int radius;
    private final int scale;
    private final int diameter;

    private final double heightFactor;
    private final double heightOffset;
    private final double heightVariationFactor;
    private final double heightVariationOffset;

    @JsonCreator
    public BiomeTerrainCache(
            @JsonProperty(value = "radius", required = true) int radius,
            @JsonProperty(value = "scale", required = true) int scale,
            @JsonProperty(value = "heightFactor", required = true) double heightFactor,
            @JsonProperty(value = "heightOffset", required = true) double heightOffset,
            @JsonProperty(value = "heightVariationFactor", required = true) double heightVariationFactor,
            @JsonProperty(value = "heightVariationOffset", required = true) double heightVariationOffset) {
        this.radius = PValidation.ensureNonNegative(radius);
        this.scale = PValidation.ensureNonNegative(scale);
        this.diameter = radius * 2 + 1;

        this.weights = new double[this.diameter * this.diameter];
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                this.weights[(dx + radius) * this.diameter + dz + radius] = 10.0d / Math.sqrt(dx * dx + dz * dz + 0.2d);
            }
        }

        this.heightFactor = heightFactor;
        this.heightOffset = heightOffset;
        this.heightVariationFactor = heightVariationFactor;
        this.heightVariationOffset = heightVariationOffset;
    }

    public Data get(int x, int z, @NonNull BiomeMap biomes) {
        Long2ObjectLinkedOpenHashMap<Data> cache = this.cacheCache.get();
        Data val = cache.getAndMoveToLast(Chunk.key(x, z));
        if (val == null) {
            if (cache.size() >= 1024) {
                cache.removeFirst();
            }

            //actually calculate data
            double smoothHeight = 0.0d;
            double smoothVariation = 0.0d;
            double totalWeight = 0.0d;

            final double heightFactor = this.heightFactor;
            final double heightOffset = this.heightOffset;
            final double heightVariationFactor = this.heightVariationFactor;
            final double heightVariationOffset = this.heightVariationOffset;

            final double centerHeight = biomes.get(x, z).getElevation().getNormalizedHeight();
            for (int radius = this.radius, scale = this.scale, diameter = this.diameter, dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    BiomeElevation elevation = biomes.get(x + dx * scale, z + dz * scale).getElevation();

                    double height = elevation.getNormalizedHeight();
                    double variation = elevation.getNormalizedVariation();

                    if (height > 0.0d) {
                        height = height * heightFactor + heightOffset;
                        variation = variation * heightVariationFactor + heightVariationOffset;
                    }

                    double weight = Math.abs(this.weights[(dx + radius) * diameter + dx + radius] / (height + 2.0d));
                    if (height > centerHeight) {
                        weight *= 0.5d;
                    }

                    smoothHeight += height * weight;
                    smoothVariation += variation * weight;
                    totalWeight += weight;
                }
            }

            cache.put(Chunk.key(x, z), val = new Data(smoothHeight / totalWeight, smoothVariation / totalWeight));
        }
        return val;
    }

    @RequiredArgsConstructor
    public static final class Data {
        public final double baseHeight;
        public final double heightVariation;
    }
}
