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
    private final int      radius;
    private final int      scale;
    private final int      diameter;

    @JsonCreator
    public BiomeTerrainCache(
            @JsonProperty(value = "radius", required = true) int radius,
            @JsonProperty(value = "scale", required = true) int scale) {
        this.radius = PValidation.ensureNonNegative(radius);
        this.scale = PValidation.ensureNonNegative(scale);
        this.diameter = radius * 2 + 1;

        this.weights = new double[this.diameter * this.diameter];
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                this.weights[(x + radius) * this.diameter + z + radius] = 10.0d / Math.sqrt(x * x + z * z + 0.2d);
            }
        }
    }

    public Data get(int x, int z, @NonNull BiomeMap biomes) {
        Long2ObjectLinkedOpenHashMap<Data> cache = this.cacheCache.get();
        Data val = cache.get(Chunk.key(x, z));
        if (val == null) {
            if (cache.size() >= 1024) {
                cache.removeFirst();
            }

            //actually calculate data
            double smoothHeight = 0.0d;
            double smoothVariation = 0.0d;
            double totalWeight = 0.0d;

            final double centerHeight = biomes.get(x, z).getElevation().getNormalizedHeight();
            for (int radius = this.radius, dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    BiomeElevation elevation = biomes.get(x + dx * this.scale, z + dz * this.scale).getElevation();

                    double height = elevation.getNormalizedHeight();
                    double variation = elevation.getNormalizedVariation();

                    double weight = Math.abs(this.weights[(dx + this.radius) * this.diameter + dx + this.radius] / (height + 2.0d));
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

        /*public Data(double baseHeight, double heightVariation)  {
            this.baseHeight = baseHeight * 17.0d / 64.0d - 1.0d / 256.0d;
            this.heightVariation = heightVariation * 2.4d + 4.0d / 15.0d;
        }*/
    }
}
