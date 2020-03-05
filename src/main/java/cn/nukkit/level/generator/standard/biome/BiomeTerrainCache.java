package cn.nukkit.level.generator.standard.biome;

import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.generator.standard.biome.map.BiomeMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.common.ref.Ref;
import net.daporkchop.lib.common.ref.ThreadRef;
import net.daporkchop.lib.common.util.PValidation;

/**
 * @author DaPorkchop_
 */
public final class BiomeTerrainCache {
    private final Ref<Long2ObjectLinkedOpenHashMap<Data>> cacheCache = ThreadRef.soft(Long2ObjectLinkedOpenHashMap::new);
    private final double[] weights;
    private final int      radius;
    private final int      diameter;

    public BiomeTerrainCache(int radius) {
        this.radius = PValidation.ensureNonNegative(radius);
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

            final double centerHeight = biomes.get(x, z).getBaseHeight();
            for (int radius = this.radius, dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    GenerationBiome biome = biomes.get(x + dx, z + dz);

                    double height = biome.getBaseHeight();
                    double variation = biome.getHeightVariation();

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
        public final double avgHeight;
        public final double variation;
    }
}
