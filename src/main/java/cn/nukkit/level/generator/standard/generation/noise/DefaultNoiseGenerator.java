package cn.nukkit.level.generator.standard.generation.noise;

import cn.nukkit.level.generator.standard.misc.DoubleTriple;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import lombok.NonNull;
import net.daporkchop.lib.noise.NoiseSource;
import net.daporkchop.lib.noise.filter.ScaleOctavesOffsetFilter;
import net.daporkchop.lib.random.PRandom;

/**
 * @author DaPorkchop_
 */
@JsonDeserialize
public abstract class DefaultNoiseGenerator implements NoiseGenerator {
    @JsonProperty
    protected DoubleTriple scale = DoubleTriple.ONE;

    @JsonProperty
    protected int octaves = 1;

    @JsonProperty
    protected double factor = 1.0d;

    @JsonProperty
    protected double offset = 0.0d;

    @Override
    public NoiseSource create(@NonNull PRandom random) {
        return new ScaleOctavesOffsetFilter(this.create0(random), this.scale.getX(), this.scale.getY(), this.scale.getZ(), this.octaves, this.factor, this.offset);
    }

    protected abstract NoiseSource create0(@NonNull PRandom random);

    @JsonSetter("octaves")
    private void setOctaves(int octaves) {
        Preconditions.checkArgument(octaves >= 1, "octaves (%d) must be at least 1!", octaves);
        this.octaves = octaves;
    }
}
