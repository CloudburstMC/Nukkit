package cn.nukkit.level.generator;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.math.ChunkPos;

import java.util.Collection;
import java.util.Random;

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
     * @param random an instance of {@link Random} for generating random numbers, initialized with a seed based on chunk's position
     * @param chunk  the chunk to generate
     * @param chunkX the chunk's X coordinate
     * @param chunkZ the chunk's Z coordinate
     * @return whether or not the chunk's dirty flag should be left as-is. Generally this should always be {@code false}, unless a very specific need is present for the chunk to never be generated more than once
     */
    boolean generate(Random random, IChunk chunk, int chunkX, int chunkZ);

    /**
     * Populates a given chunk.
     *
     * @param random an instance of {@link Random} for generating random numbers, initialized with a seed based on chunk's position
     * @param level  a {@link ChunkManager} containing only the chunks at the relative positions specified by {@link #populationChunks(ChunkPos, int, int)}
     * @param chunkX the chunk's X coordinate
     * @param chunkZ the chunk's Z coordinate
     * @return whether or not that the chunk's dirty flag, and that of all the population chunks, should be left as-is. Generally this should always be {@code false}, unless a very specific need is present for the chunk to never be populated more than once
     */
    boolean populate(Random random, ChunkManager level, int chunkX, int chunkZ);

    /**
     * Gets a list of absolute chunk positions. The chunks at these positions will be loaded/generated if needed, and will be the only chunks present in
     * the {@link ChunkManager} instance passed to {@link #populate(Random, ChunkManager, int, int)}.
     * <p>
     * The chunk being populated itself is always implicitly included, but may be explicitly specified with no side effects.
     *
     * @param pos    the chunk's position (provided for convenience)
     * @param chunkX the chunk's X coordinate
     * @param chunkZ the chunk's Z coordinate
     * @return the positions of the chunks that are required for the given chunk to be populated
     */
    Collection<ChunkPos> populationChunks(ChunkPos pos, int chunkX, int chunkZ);
}
