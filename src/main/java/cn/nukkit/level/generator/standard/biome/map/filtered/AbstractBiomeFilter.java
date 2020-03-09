package cn.nukkit.level.generator.standard.biome.map.filtered;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.daporkchop.lib.common.ref.Ref;
import net.daporkchop.lib.common.ref.ThreadRef;
import net.daporkchop.lib.random.PRandom;
import net.daporkchop.lib.random.impl.FastPRandom;

import java.util.Objects;

import static net.daporkchop.lib.random.impl.FastPRandom.*;

/**
 * Base implementation of a {@link BiomeFilter}.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public abstract class AbstractBiomeFilter implements BiomeFilter {
    protected long seed;

    private final Ref<FastPRandom> randomCache = ThreadRef.soft(FastPRandom::new);

    @Override
    public void init(long seed, PRandom random) {
        this.seed = random.nextLong();
    }

    protected int random(int x, int z, int i, int bound) {
        return (mix32(mix64(mix64(mix64(this.seed + i) + x) + z)) >>> 1) % bound;
    }

    protected PRandom random() {
        return this.randomCache.get();
    }

    protected void updateSeed(PRandom random, int x, int z)  {
        ((FastPRandom) random).setSeed(mix64(mix64(this.seed + x) + z));
    }

    @JsonDeserialize
    protected abstract static class Next extends AbstractBiomeFilter {
        @JsonProperty
        @JsonAlias({"parent"})
        protected BiomeFilter next;

        @Override
        public void init(long seed, PRandom random) {
            Objects.requireNonNull(this.next, "next must be set!");

            super.init(seed, random);

            this.next.init(seed, random);
        }
    }
}
