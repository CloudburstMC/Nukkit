package cn.nukkit.level.generator;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.math.ChunkPos;
import net.daporkchop.lib.random.PRandom;

import java.util.Collection;

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
     * @param level  a {@link ChunkManager} containing only the chunks at the relative positions specified by {@link #populationChunks(ChunkPos, int, int)}
     * @param chunkX the chunk's X coordinate
     * @param chunkZ the chunk's Z coordinate
     */
    void populate(PRandom random, ChunkManager level, int chunkX, int chunkZ);

    /**
     * Gets a list of absolute chunk positions. The chunks at these positions will be loaded/generated if needed, and will be the only chunks present in
     * the {@link ChunkManager} instance passed to {@link #populate(PRandom, ChunkManager, int, int)}.
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
