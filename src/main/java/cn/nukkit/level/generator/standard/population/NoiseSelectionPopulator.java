package cn.nukkit.level.generator.standard.population;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.level.generator.standard.generation.decorator.SurfaceDecorator;
import cn.nukkit.level.generator.standard.generation.noise.NoiseGenerator;
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
 * Similar to {@link SurfaceDecorator}, but switches between two different populators based on the output of a noise function.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public class NoiseSelectionPopulator extends AbstractGenerationPass implements Populator {
    public static final Identifier ID = Identifier.fromString("nukkitx:noise");

    protected NoiseSource selector;

    @JsonProperty
    protected double randomFactor = 0.0d;

    @JsonProperty
    protected double min = Double.NaN;

    @JsonProperty
    protected double max = Double.NaN;

    @JsonProperty
    protected Populator[] below = Populator.EMPTY_ARRAY;

    @JsonProperty
    protected Populator[] above = Populator.EMPTY_ARRAY;

    @JsonProperty
    protected Populator[] in = Populator.EMPTY_ARRAY;

    @JsonProperty("selector")
    protected NoiseGenerator selectorNoise;

    @Override
    protected void init0(long levelSeed, long localSeed, StandardGenerator generator) {
        PRandom random = new FastPRandom(localSeed);

        Preconditions.checkState(!Double.isNaN(this.min), "min must be set!");
        Preconditions.checkState(!Double.isNaN(this.max), "max must be set!");
        this.selector = Objects.requireNonNull(this.selectorNoise, "selector must be set!").create(random);
        this.selectorNoise = null;

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
        double noise = this.selector.get(blockX, blockZ) + random.nextDouble() * this.randomFactor;
        for (Populator populator : noise < this.min ? this.below : noise > this.max ? this.above : this.in) {
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
