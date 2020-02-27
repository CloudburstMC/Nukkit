package cn.nukkit.level.generator.standard.gen;

import cn.nukkit.level.chunk.IChunk;
import net.daporkchop.lib.random.PRandom;

/**
 * Allows individual modification of blocks in a chunk after surfaces have been built.
 * <p>
 * Similar to a populator, but only operates on a single chunk.
 *
 * @author DaPorkchop_
 */
public interface Decorator {
    /**
     * Decorates a given chunk.
     *  @param chunk  the chunk to be decorated
     * @param random an instance of {@link PRandom} for generating random numbers, initialized with a seed based on chunk's position
     * @param x
     * @param z
     */
    void decorate(IChunk chunk, PRandom random, int x, int z);
}
