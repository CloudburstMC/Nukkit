package com.nukkitx.api.level.chunk.generator;

import com.nukkitx.api.Server;

@FunctionalInterface
public interface ChunkGeneratorFactory {
    /**
     * Returns a new instance of the ChunkGenerator.
     *
     * @param server server
     * @return new generator
     */
    ChunkGenerator createChunkGenerator(Server server);
}
