package cn.nukkit.level.generator.standard.gen.decorator;

import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.level.generator.standard.gen.noise.NoiseGenerator;
import cn.nukkit.level.generator.standard.misc.AbstractGenerationPass;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import net.daporkchop.lib.noise.NoiseSource;
import net.daporkchop.lib.random.PRandom;
import net.daporkchop.lib.random.impl.FastPRandom;

import java.util.Objects;

/**
 * Similar to {@link SurfaceDecorator}, but switches between two different decorators based on the output of a noise function.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public class NoiseSelectionDecorator extends AbstractGenerationPass implements Decorator {
    public static final Identifier ID = Identifier.fromString("nukkitx:noise");

    protected NoiseSource selector;

    @JsonProperty
    protected double min = Double.NaN;

    @JsonProperty
    protected double max = Double.NaN;

    @JsonProperty
    protected Decorator[] below = Decorator.EMPTY_ARRAY;

    @JsonProperty
    protected Decorator[] above = Decorator.EMPTY_ARRAY;

    @JsonProperty
    protected Decorator[] in = Decorator.EMPTY_ARRAY;

    @JsonProperty("selector")
    protected NoiseGenerator selectorNoise;

    @Override
    protected void init0(long levelSeed, long localSeed, StandardGenerator generator) {
        Preconditions.checkState(!Double.isNaN(this.min), "min must be set!");
        Preconditions.checkState(!Double.isNaN(this.max), "max must be set!");
        this.selector = Objects.requireNonNull(this.selectorNoise, "selector must be set!").create(new FastPRandom(localSeed));
        this.selectorNoise = null;

        for (Decorator decorator : Objects.requireNonNull(this.below, "below must be set!"))  {
            decorator.init(levelSeed, localSeed, generator);
        }
        for (Decorator decorator : Objects.requireNonNull(this.above, "above must be set!"))  {
            decorator.init(levelSeed, localSeed, generator);
        }
        for (Decorator decorator : Objects.requireNonNull(this.in, "in must be set!"))  {
            decorator.init(levelSeed, localSeed, generator);
        }
    }

    @Override
    public void decorate(IChunk chunk, PRandom random, int x, int z) {
        double noise = this.selector.get((chunk.getX() << 4) + x, (chunk.getZ() << 4) + z);
        for (Decorator decorator : noise < this.min ? this.below : noise > this.max ? this.above : this.in) {
            decorator.decorate(chunk, random, x, z);
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
