package cn.nukkit.level.generator.standard.biome.map;

import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.generator.standard.biome.GenerationBiome;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import lombok.NonNull;

/**
 * Implementation of {@link BiomeMap} which caches the biomes looked up at a given position.
 * <p>
 * Not thread-safe.
 *
 * @author DaPorkchop_
 */
public final class CachingBiomeMap implements BiomeMap {
    private final Long2ObjectMap<GenerationBiome> cache = new Long2ObjectOpenHashMap<>();
    private BiomeMap delegate;

    @Override
    public GenerationBiome get(int x, int z) {
        long key = Chunk.key(x, z);
        GenerationBiome biome = this.cache.get(key);
        if (biome == null) {
            this.cache.put(key, biome = this.delegate.get(x, z));
        }
        return biome;
    }

    public CachingBiomeMap init(@NonNull BiomeMap delegate) {
        if (this.delegate != null) {
            this.clear();
        }
        this.delegate = delegate;
        return this;
    }

    public void clear() {
        this.cache.clear();
        this.delegate = null;
    }
}
