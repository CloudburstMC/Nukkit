package cn.nukkit.level.generator.standard.pop;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.level.generator.standard.gen.decorator.SurfaceDecorator;
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
 * Similar to {@link SurfaceDecorator}, but switches between two different populators based on the output of a noise function.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public class NoiseSelectionPopulator extends AbstractGenerationPass implements Populator {
    public static final Identifier ID = Identifier.fromString("nukkitx:noise");

    protected NoiseSource selector;

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
        Preconditions.checkState(!Double.isNaN(this.min), "min must be set!");
        Preconditions.checkState(!Double.isNaN(this.max), "max must be set!");
        this.selector = Objects.requireNonNull(this.selectorNoise, "selector must be set!").create(new FastPRandom(localSeed));
        this.selectorNoise = null;

        for (Populator populator : Objects.requireNonNull(this.below, "below must be set!")) {
            populator.init(levelSeed, localSeed, generator);
        }
        for (Populator populator : Objects.requireNonNull(this.above, "above must be set!")) {
            populator.init(levelSeed, localSeed, generator);
        }
        for (Populator populator : Objects.requireNonNull(this.in, "in must be set!")) {
            populator.init(levelSeed, localSeed, generator);
        }
    }

    @Override
    public void populate(PRandom random, ChunkManager level, int chunkX, int chunkZ) {
        double noise = this.selector.get(chunkX << 4, chunkZ << 4);
        for (Populator populator : noise < this.min ? this.below : noise > this.max ? this.above : this.in) {
            populator.populate(random, level, chunkX, chunkZ);
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
