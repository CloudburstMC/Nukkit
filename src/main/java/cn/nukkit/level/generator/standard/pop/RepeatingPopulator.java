package cn.nukkit.level.generator.standard.pop;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.standard.misc.IntRange;
import cn.nukkit.utils.ConfigSection;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import lombok.NonNull;
import net.daporkchop.lib.common.util.PValidation;
import net.daporkchop.lib.random.PRandom;

/**
 * A populator that runs multiple times.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public abstract class RepeatingPopulator implements Populator {
    @JsonProperty(required = true)
    protected IntRange tries;

    @Override
    public void populate(PRandom random, ChunkManager level, int chunkX, int chunkZ) {
        int blockX = chunkX << 4;
        int blockZ = chunkZ << 4;

        for (int i = this.tries.rand(random) - 1; i >= 0; i--) {
            this.tryPopulate(random, level, random.nextInt(blockX, blockX + 16), random.nextInt(blockZ, blockZ + 16));
        }
    }

    /**
     * Actually does population.
     *
     * @param x the X coordinate to attempt population at (in blocks)
     * @param z the Z coordinate to attempt population at (in blocks)
     * @see #populate(PRandom, ChunkManager, int, int)
     */
    protected abstract void tryPopulate(PRandom random, ChunkManager level, int x, int z);
}
