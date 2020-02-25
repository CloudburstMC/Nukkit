package cn.nukkit.level.generator.standard.pop;

import cn.nukkit.level.ChunkManager;
import net.daporkchop.lib.random.PRandom;

/**
 * Sets individual blocks of a chunk and its neighbors, allowing to generate larger structures and features.
 *
 * @author DaPorkchop_
 */
@FunctionalInterface
public interface Populator {
    /**
     * Populates a given chunk.
     *
     * @param random an instance of {@link PRandom} for generating random numbers, initialized with a seed based on chunk's position
     * @param level  a {@link ChunkManager} containing only a 3x3 square of generated chunks, centered around the chunk being populated
     * @param chunkX the chunk's X coordinate
     * @param chunkZ the chunk's Z coordinate
     */
    void populate(PRandom random, ChunkManager level, int chunkX, int chunkZ);
}
