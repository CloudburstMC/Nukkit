package cn.nukkit.level.generator.standard.pop;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.level.generator.standard.misc.IntRange;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.daporkchop.lib.random.PRandom;

import java.util.Objects;

/**
 * A populator that runs multiple times.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public abstract class RepeatingPopulator implements Populator {
    @JsonProperty
    protected IntRange count;

    @Override
    public void init(long levelSeed, long localSeed, StandardGenerator generator) {
        Objects.requireNonNull(this.count, "count must be set!");
    }

    @Override
    public void populate(PRandom random, ChunkManager level, int chunkX, int chunkZ) {
        for (int i = this.count.rand(random) - 1; i >= 0; i--) {
            this.tryPopulate(random, level, (chunkX << 4) + random.nextInt(16), (chunkZ << 4) + random.nextInt(16));
        }
    }

    /**
     * Actually does population.
     *
     * @param x the X coordinate to attempt population at (in blocks)
     * @param z the Z coordinate to attempt population at (in blocks)
     * @see Populator#populate(PRandom, ChunkManager, int, int)
     */
    protected abstract void tryPopulate(PRandom random, ChunkManager level, int x, int z);

    @Override
    public abstract Identifier getId();
}
