package com.nukkitx.server.level.biome.decorator;

import com.nukkitx.api.level.chunk.Chunk;
import com.nukkitx.server.level.populator.ChunkPopulator;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Contains more detailed information about biomes for use in terrain generation
 *
 * @author DaPorkchop_
 */
@Data
public abstract class BiomeDecorator {
    /**
     * Additional chunk populators for this chunk
     */
    @Getter(AccessLevel.PRIVATE)
    private final Set<ChunkPopulator> populators = new HashSet<>();

    /**
     * The minimum height for terrain generation
     */
    private final int minHeight;

    /**
     * The maximum height for terrain generation
     */
    private final int maxHeight;

    /**
     * Places decorate on a chunk
     *
     * @param chunk  the chunk to decorate
     * @param random
     */
    public void decorate(@NonNull Chunk chunk, @NonNull Random random) {
        this.populators.forEach(populator -> populator.populate(chunk.getLevel(), chunk, random));
    }

    /**
     * Adds a chunk populator that will be executed when populating this chunk
     *
     * @param populator the populator to add
     */
    protected final void addPopulator(@NonNull ChunkPopulator populator) {
        this.populators.add(populator);
    }
}
