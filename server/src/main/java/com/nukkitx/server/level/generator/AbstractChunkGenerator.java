package com.nukkitx.server.level.generator;

import com.nukkitx.api.level.Level;
import com.nukkitx.api.level.chunk.Chunk;
import com.nukkitx.api.level.chunk.generator.ChunkGenerator;
import com.nukkitx.server.level.populator.ChunkPopulator;
import lombok.NonNull;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Handles all populator-related code for chunk generators
 *
 * @author DaPorkchop_
 */
public abstract class AbstractChunkGenerator implements ChunkGenerator {
    private final Set<ChunkPopulator> populators = new HashSet<>();

    protected final void addPopulator(@NonNull ChunkPopulator populator)  {
        this.populators.add(populator);
    }

    @Override
    public final void populateChunk(Level level, Chunk chunk, Random random) {
        this.populators.forEach(populator -> populator.populate(level, chunk, random));
    }
}
