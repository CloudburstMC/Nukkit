package com.nukkitx.api.level.chunk.generator;

import com.nukkitx.api.Server;

public interface ChunkGeneratorFactory {
    /**
     * Returns a new instance of the ChunkGenerator.
     *
     * @return new generator
     */
    ChunkGenerator createChunkGenerator(Server server);
}
