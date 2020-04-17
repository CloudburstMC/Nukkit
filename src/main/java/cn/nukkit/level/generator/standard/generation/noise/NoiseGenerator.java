package cn.nukkit.level.generator.standard.generation.noise;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.NonNull;
import net.daporkchop.lib.noise.NoiseSource;
import net.daporkchop.lib.random.PRandom;

/**
 * Creates instances of {@link NoiseSource}.
 *
 * @author DaPorkchop_
 */
@FunctionalInterface
@JsonDeserialize(using = NoiseGeneratorDeserializer.class)
public interface NoiseGenerator {
    /**
     * Creates a new {@link NoiseSource} using the given {@link PRandom}.
     *
     * @param random an instance of {@link PRandom} to use for generating random numbers
     * @return a new {@link NoiseSource}
     */
    NoiseSource create(@NonNull PRandom random);
}
