package cn.nukkit.level.generator.standard.biome.map.filtered;

import net.daporkchop.lib.random.PRandom;

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
        long l = (this.seed + i) * 6364136223846793005L + 1442695040888963407L;
        int val = (int) (((l + x) * 6364136223846793005L + 1442695040888963407L + z) % bound);
        return val < 0 ? val + bound : val;
    }
}
