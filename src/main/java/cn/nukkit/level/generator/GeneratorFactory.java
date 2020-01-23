package cn.nukkit.level.generator;

import lombok.NonNull;

/**
 * Creates {@link Generator} instances.
 *
 * @author DaPorkchop_
 */
@FunctionalInterface
public interface GeneratorFactory {
    /**
     * Creates a new {@link Generator} using the given seed and options.
     * <p>
     * The returned {@link Generator} must be ready to use before being returned.
     *
     * @param seed    the seed of the world being generated
     * @param options an instance of {@link GeneratorOptions} containing the configured options for the generator
     * @return a newly created {@link Generator}
     */
    Generator create(long seed, @NonNull GeneratorOptions options);
}
