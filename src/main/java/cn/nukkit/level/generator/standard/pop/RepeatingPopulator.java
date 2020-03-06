package cn.nukkit.level.generator.standard.pop;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.standard.misc.IntRange;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.daporkchop.lib.random.PRandom;

/**
 * A populator that runs multiple times.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public abstract class RepeatingPopulator implements Populator {
    @JsonProperty(required = true)
    protected IntRange count;

    @Override
    public void populate(PRandom random, ChunkManager level, int chunkX, int chunkZ, int blockX, int blockZ) {
        for (int i = this.count.rand(random) - 1; i >= 0; i--) {
            this.tryPopulate(random, level, blockX + random.nextInt(16), blockZ + random.nextInt(16));
        }
    }

    /**
     * Actually does population.
     *
     * @param x the X coordinate to attempt population at (in blocks)
     * @param z the Z coordinate to attempt population at (in blocks)
     * @see Populator#populate(PRandom, ChunkManager, int, int, int, int)
     */
    protected abstract void tryPopulate(PRandom random, ChunkManager level, int x, int z);

    @Override
    public abstract Identifier getId();
}
