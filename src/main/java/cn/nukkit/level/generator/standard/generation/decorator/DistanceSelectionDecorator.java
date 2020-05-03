package cn.nukkit.level.generator.standard.generation.decorator;

import cn.nukkit.level.chunk.IChunk;
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
@Decorator.SkipRegistrationAsPopulator
@JsonDeserialize
public class DistanceSelectionDecorator extends AbstractGenerationPass implements Decorator {
    public static final Identifier ID = Identifier.fromString("nukkitx:distance");

    protected double minSq;
    protected double maxSq;

    @JsonProperty
    protected Decorator[] below = Decorator.EMPTY_ARRAY;

    @JsonProperty
    protected Decorator[] above = Decorator.EMPTY_ARRAY;

    @JsonProperty
    protected Decorator[] in = Decorator.EMPTY_ARRAY;

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

        for (Decorator decorator : Objects.requireNonNull(this.below, "below must be set!")) {
            decorator.init(levelSeed, random.nextLong(), generator);
        }
        for (Decorator decorator : Objects.requireNonNull(this.above, "above must be set!")) {
            decorator.init(levelSeed, random.nextLong(), generator);
        }
        for (Decorator decorator : Objects.requireNonNull(this.in, "in must be set!")) {
            decorator.init(levelSeed, random.nextLong(), generator);
        }
    }

    @Override
    public void decorate(PRandom random, IChunk chunk, int x, int z) {
        int blockX = (chunk.getX() << 4) | x;
        int blockZ = (chunk.getZ() << 4) | z;
        double distanceSq = blockX * blockX + blockZ * blockZ;
        for (Decorator decorator : distanceSq < this.minSq ? this.below : distanceSq > this.maxSq ? this.above : this.in) {
            decorator.decorate(random, chunk, x, z);
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
    private void setOut(Decorator[] out) {
        this.above = this.below = out;
    }
}
