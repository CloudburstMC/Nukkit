package cn.nukkit.api.level.chunk.generator;

import cn.nukkit.api.Server;

public interface ChunkGeneratorFactory {
    /**
     * Returns a new instance of the ChunkGenerator.
     *
     * @return new generator
     */
    ChunkGenerator createChunkGenerator(Server server);
}
