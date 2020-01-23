package cn.nukkit.level.generator;

import cn.nukkit.level.chunk.IChunk;
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
     * @param random an instance of {@link Random} for generating random numbers
     * @param chunk the {@link IChunk} to generate
     */
    void generate(@NonNull Random random, @NonNull IChunk chunk);
}
