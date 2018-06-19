package com.nukkitx.server.level.biome;

import com.nukkitx.api.level.chunk.Chunk;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

/**
 * Contains more detailed information about biomes for use in terrain generation
 *
 * @author DaPorkchop_
 */
@Data
public abstract class BiomeImpl {
    /**
     * The minimum height for terrain generation
     */
    @Getter
    private final int minHeight;

    /**
     * The maximum height for terrain generation
     */
    @Getter
    private final int maxHeight;

    /**
     * Places cover on a chunk
     *
     * @param chunk     the chunk to cover
     * @param x         the in-chunk x coordinate to cover
     * @param z         the in-chunk z coordinate to cover
     */
    public abstract void cover(@NonNull Chunk chunk, int x, int z);
}
