package cn.nukkit.level.generator.standard.gen.decorator;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.standard.misc.GenerationPass;
import cn.nukkit.level.generator.standard.pop.Populator;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.daporkchop.lib.random.PRandom;

/**
 * Allows individual modification of blocks in a chunk after surfaces have been built.
 * <p>
 * Similar to a populator, but only operates on an individual block column in a single chunk.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize(using = DecoratorDeserializer.class)
public interface Decorator extends Populator {
    Decorator[] EMPTY_ARRAY = new Decorator[0];

    @Override
    default void populate(PRandom random, ChunkManager level, int chunkX, int chunkZ) {
        IChunk chunk = level.getChunk(chunkX, chunkZ);
        for (int x = 0; x < 16; x++)    {
            for (int z = 0; z < 16; z++)    {
                this.decorate(chunk, random, x, z);
            }
        }
    }

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
