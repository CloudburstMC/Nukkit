package cn.nukkit.level.generator.standard.misc;

import cn.nukkit.level.chunk.Chunk;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import it.unimi.dsi.fastutil.longs.Long2LongLinkedOpenHashMap;
import net.daporkchop.lib.common.ref.Ref;
import net.daporkchop.lib.common.ref.ThreadRef;
import net.daporkchop.lib.common.util.PValidation;

/**
 * @author DaPorkchop_
 */
@JsonDeserialize
public abstract class TerrainDoubleCache {
    protected static final long NaN = Double.doubleToRawLongBits(Double.NaN);

    protected final Ref<Long2LongLinkedOpenHashMap> cacheCache = ThreadRef.soft(Long2LongLinkedOpenHashMap::new);
    protected final int radius;
    protected final int scale;

    @JsonCreator
    public TerrainDoubleCache(
            @JsonProperty(value = "radius", required = true) int radius,
            @JsonProperty(value = "scale", required = true) int scale) {
        this.radius = PValidation.ensureNonNegative(radius);
        this.scale = PValidation.ensureNonNegative(scale);
    }

    public double get(int x, int z) {
        Long2LongLinkedOpenHashMap cache = this.cacheCache.get();
        long val = cache.getOrDefault(Chunk.key(x, z), NaN);
        if (val == NaN) {
            if (cache.size() >= 1024) {
                cache.removeFirstLong();
            }

            cache.put(Chunk.key(x, z), val = Double.doubleToRawLongBits(this.computeValue(x, z, this.radius, this.scale)));
        }
        return Double.longBitsToDouble(val);
    }

    protected abstract double computeValue(int x, int z, int radius, int scale);
}
