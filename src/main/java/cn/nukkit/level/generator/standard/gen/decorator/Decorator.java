package cn.nukkit.level.generator.standard.gen.decorator;

import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.standard.misc.GenerationPass;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.daporkchop.lib.random.PRandom;

/**
 * Allows individual modification of blocks in a chunk after surfaces have been built.
 * <p>
 * Similar to a populator, but only operates on a single chunk.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize(using = DecoratorDeserializer.class)
public interface Decorator extends GenerationPass {
    Decorator[] EMPTY_ARRAY = new Decorator[0];

    /**
     * Decorates a given chunk.
     *
     * @param chunk  the chunk to be decorated
     * @param random an instance of {@link PRandom} for generating random numbers, initialized with a seed based on chunk's position
     * @param x      the X coordinate of the block column in the chunk to decorate
     * @param z      the Z coordinate of the block column in the chunk to decorate
     */
    void decorate(IChunk chunk, PRandom random, int x, int z);

    @Override
    Identifier getId();
}
