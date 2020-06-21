package cn.nukkit.level.generator.populator.type;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.NukkitRandom;

/**
 * @author DaPorkchop_
 * <p>
 * A populator that generates an object a certain amount of times.
 * This prevents the exact same code from being repeated in nearly every single populator
 */
public abstract class PopulatorCount extends Populator {

    private int randomAmount;

    private int baseAmount;

    public final void setRandomAmount(final int randomAmount) {
        this.randomAmount = randomAmount + 1;
    }

    public final void setBaseAmount(final int baseAmount) {
        this.baseAmount = baseAmount;
    }

    @Override
    public final void populate(final ChunkManager level, final int chunkX, final int chunkZ, final NukkitRandom random, final FullChunk chunk) {
        final int count = this.baseAmount + random.nextBoundedInt(this.randomAmount);
        for (int i = 0; i < count; i++) {
            this.populateCount(level, chunkX, chunkZ, random, chunk);
        }
    }

    protected abstract void populateCount(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk);

}
