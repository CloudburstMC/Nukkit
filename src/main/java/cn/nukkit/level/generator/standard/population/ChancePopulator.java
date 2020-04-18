package cn.nukkit.level.generator.standard.population;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.level.generator.standard.misc.AbstractGenerationPass;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import net.daporkchop.lib.random.PRandom;

/**
 * A populator that has a certain probability of running every time it finds a viable location.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public abstract class ChancePopulator extends AbstractGenerationPass implements Populator {
    @JsonProperty
    protected double chance = Double.NaN;

    @Override
    protected void init0(long levelSeed, long localSeed, StandardGenerator generator) {
        Preconditions.checkState(!Double.isNaN(this.chance), "chance must be set!");
        Preconditions.checkState(this.chance >= 0.0d && this.chance <= 1.0d, "chance (%s) must be in range 0-1!", this.chance);
    }

    public abstract static class Column extends ChancePopulator {
        @Override
        public final void populate(PRandom random, ChunkManager level, int blockX, int blockZ) {
            if (random.nextDouble() < this.chance) {
                this.populate0(random, level, blockX, blockZ);
            }
        }

        protected abstract void populate0(PRandom random, ChunkManager level, int blockX, int blockZ);
    }
}
