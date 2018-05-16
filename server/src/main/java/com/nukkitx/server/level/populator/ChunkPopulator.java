package com.nukkitx.server.level.populator;

import com.nukkitx.api.level.Level;
import com.nukkitx.api.level.chunk.Chunk;

import java.util.Random;

/**
 * Populates a chunk after generation
 *
 * @author DaPorkchop_
 */
public interface ChunkPopulator {
    /**
     * Populates a chunk
     *
     * @param level  the level that the chunk to be populated is in
     * @param chunk  the chunk to populate
     * @param random an instance of {@link Random}, for use in population
     */
    void populate(Level level, Chunk chunk, Random random);
}
