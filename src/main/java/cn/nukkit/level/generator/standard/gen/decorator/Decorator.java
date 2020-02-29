package cn.nukkit.level.generator.standard.gen.decorator;

import cn.nukkit.level.chunk.IChunk;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.daporkchop.lib.random.PRandom;

/**
 * Allows individual modification of blocks in a chunk after surfaces have been built.
 * <p>
 * Similar to a populator, but only operates on a single chunk.
 *
 * @author DaPorkchop_
 */
@FunctionalInterface
@JsonDeserialize(using = DecoratorDeserializer.class)
public interface Decorator {
    Decorator[] EMPTY_ARRAY = new Decorator[0];

    /**
     * Decorates a given chunk.
     *
     * @param chunk  the chunk to be decorated
     * @param random an instance of {@link PRandom} for generating random numbers, initialized with a seed based on chunk's position
     */
    void decorate(IChunk chunk, PRandom random, int x, int z);
}
