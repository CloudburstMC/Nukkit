package cn.nukkit.level.generator.standard.population;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.level.generator.standard.misc.AbstractGenerationPass;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import net.daporkchop.lib.random.PRandom;
import net.daporkchop.lib.random.impl.FastPRandom;

import java.util.Objects;

/**
 * @author DaPorkchop_
 */
@JsonDeserialize
public class DistanceSelectionPopulator extends AbstractGenerationPass implements Populator {
    public static final Identifier ID = Identifier.fromString("nukkitx:distance");

    protected double minSq;
    protected double maxSq;

    @JsonProperty
    protected Populator[] below = Populator.EMPTY_ARRAY;

    @JsonProperty
    protected Populator[] above = Populator.EMPTY_ARRAY;

    @JsonProperty
    protected Populator[] in = Populator.EMPTY_ARRAY;

    @JsonProperty
    protected double min = Double.NaN;

    @JsonProperty
    protected double max = Double.NaN;

    @Override
    protected void init0(long levelSeed, long localSeed, StandardGenerator generator) {
        PRandom random = new FastPRandom(localSeed);

        Preconditions.checkState(!Double.isNaN(this.min), "min must be set!");
        this.minSq = this.min * this.min;
        Preconditions.checkState(!Double.isNaN(this.max), "max must be set!");
        this.maxSq = this.max * this.max;

        for (Populator populator : Objects.requireNonNull(this.below, "below must be set!")) {
            populator.init(levelSeed, random.nextLong(), generator);
        }
        for (Populator populator : Objects.requireNonNull(this.above, "above must be set!")) {
            populator.init(levelSeed, random.nextLong(), generator);
        }
        for (Populator populator : Objects.requireNonNull(this.in, "in must be set!")) {
            populator.init(levelSeed, random.nextLong(), generator);
        }
    }

    @Override
    public void populate(PRandom random, ChunkManager level, int blockX, int blockZ) {
        double distanceSq = blockX * blockX + blockZ * blockZ;
        for (Populator populator : distanceSq < this.minSq ? this.below : distanceSq > this.maxSq ? this.above : this.in) {
            populator.populate(random, level, blockX, blockZ);
        }
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @JsonSetter("threshold")
    private void setThreshold(double threshold) {
        this.min = this.max = threshold;
    }

    @JsonSetter("out")
    private void setOut(Populator[] out) {
        this.above = this.below = out;
    }
}
