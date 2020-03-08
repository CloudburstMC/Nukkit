package cn.nukkit.level.generator.standard.biome.map.filtered;

import net.daporkchop.lib.random.PRandom;

import static net.daporkchop.lib.random.impl.FastPRandom.*;

/**
 * @author DaPorkchop_
 */
public abstract class AbstractBiomeFilter implements BiomeFilter {
    protected long seed;

    @Override
    public void init(long seed, PRandom random) {
        this.seed = random.nextLong();
    }

    protected int random(int x, int z, int i, int bound) {
        return (mix32(mix64(mix64(mix64(this.seed + i) + x) + z)) >>> 1) % bound;
    }
}
