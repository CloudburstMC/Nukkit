package cn.nukkit.level.generator.standard.pop;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.utils.ConfigSection;
import com.google.common.base.Preconditions;
import lombok.NonNull;
import net.daporkchop.lib.common.util.PValidation;
import net.daporkchop.lib.random.PRandom;

/**
 * A populator that runs multiple times.
 *
 * @author DaPorkchop_
 */
public abstract class RepeatingPopulator implements Populator {
    protected final int minTries;
    protected final int maxTries;

    public RepeatingPopulator(@NonNull ConfigSection config, @NonNull PRandom random) {
        this.minTries = PValidation.ensureNonNegative(config.getInt("minTries", -1));
        this.maxTries = PValidation.ensurePositive(config.getInt("maxTries", this.minTries) + 1);
        Preconditions.checkArgument(this.minTries < this.maxTries, "minTries (%d) must be less than maxTries (%d)", this.minTries, this.maxTries);
    }

    @Override
    public void populate(PRandom random, ChunkManager level, int chunkX, int chunkZ) {
        int blockX = chunkX << 4;
        int blockZ = chunkZ << 4;

        for (int i = random.nextInt(this.minTries, this.maxTries) - 1; i >= 0; i--) {
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
