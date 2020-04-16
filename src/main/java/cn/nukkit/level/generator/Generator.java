package cn.nukkit.level.generator;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import net.daporkchop.lib.random.PRandom;

/**
 * Generates terrain in a level.
 * <p>
 * An implementation of {@link Generator} is expected to be able to generate and populate chunks on multiple threads concurrently.
 *
 * @author DaPorkchop_
 */
public interface Generator {
    /**
     * Generates a given chunk.
     *
     * @param random an instance of {@link PRandom} for generating random numbers, initialized with a seed based on chunk's position
     * @param chunk  the chunk to generate
     * @param chunkX the chunk's X coordinate
     * @param chunkZ the chunk's Z coordinate
     */
    void generate(PRandom random, IChunk chunk, int chunkX, int chunkZ);

    /**
     * Populates a given chunk.
     *
     * @param random an instance of {@link PRandom} for generating random numbers, initialized with a seed based on chunk's position
     * @param level  a {@link ChunkManager} containing only a 3x3 square of generated chunks, centered around the chunk being populated
     * @param chunkX the chunk's X coordinate
     * @param chunkZ the chunk's Z coordinate
     */
    void populate(PRandom random, ChunkManager level, int chunkX, int chunkZ);

    /**
     * Finishes a given chunk.
     * <p>
     * This is identical in every respect to population, except it requires that the chunk and all of its neighbors have been populated rather than
     * generated. This phase is intended for things like placing a layer of snow over cold biomes, where overlapping blocks from populated neighbors
     * can cause inconsistent/unexpected results.
     *
     * @param random an instance of {@link PRandom} for generating random numbers, initialized with a seed based on chunk's position
     * @param level  a {@link ChunkManager} containing only a 3x3 square of generated chunks, centered around the chunk being populated
     * @param chunkX the chunk's X coordinate
     * @param chunkZ the chunk's Z coordinate
     */
    void finish(PRandom random, ChunkManager level, int chunkX, int chunkZ);
}
