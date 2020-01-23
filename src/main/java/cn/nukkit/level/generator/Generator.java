package cn.nukkit.level.generator;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.math.ChunkPos;
import lombok.NonNull;

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
     * @param random an instance of {@link Random} for generating random numbers
     * @param chunk  the chunk to generate
     */
    void generate(@NonNull Random random, @NonNull IChunk chunk);

    /**
     * Populates a given chunk.
     *
     * @param random an instance of {@link Random} for generating random numbers
     * @param chunk  the chunk to populate
     * @param level  a {@link ChunkManager} containing only the chunks at the relative positions specified by {@link #populationChunks(IChunk)}
     */
    void populate(@NonNull Random random, @NonNull IChunk chunk, @NonNull ChunkManager level);

    /**
     * Gets a list of relative (!) chunk positions to the given chunk. The chunks at these positions will be generated and loaded, and will be the
     * only chunks present in the {@link ChunkManager} instance passed to {@link #populate(Random, IChunk, ChunkManager)}.
     * <p>
     * The position {@code 0,0} (which is the chunk being populated itself) is always implicitly included, but may be explicitly specified with no
     * side effects.
     *
     * @param chunk the chunk to be populated
     * @return relative positions of chunks that are required for the given chunk to be populated
     */
    Iterable<ChunkPos> populationChunks(@NonNull IChunk chunk);
}
