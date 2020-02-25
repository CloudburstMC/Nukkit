package cn.nukkit.level.generator.standard.gen;

import cn.nukkit.utils.ConfigSection;
import lombok.NonNull;
import net.daporkchop.lib.noise.NoiseSource;
import net.daporkchop.lib.random.PRandom;

import java.util.function.BiFunction;

/**
 * Creates instances of {@link NoiseSource} from their configuration.
 *
 * @author DaPorkchop_
 */
public interface NoiseSourceFactory extends BiFunction<ConfigSection, PRandom, NoiseSource> {
    /**
     * Creates a new {@link NoiseSource} from the given config.
     *
     * @param config the {@link ConfigSection} containing the config to use
     * @param random an instance of {@link PRandom} for generating random numbers
     * @return a new {@link NoiseSource} with the given configuration
     */
    @Override
    NoiseSource apply(@NonNull ConfigSection config, @NonNull PRandom random);
}
