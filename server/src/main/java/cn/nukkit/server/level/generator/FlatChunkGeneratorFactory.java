package cn.nukkit.server.level.generator;

import cn.nukkit.api.Server;
import cn.nukkit.api.level.chunk.generator.ChunkGenerator;
import cn.nukkit.api.level.chunk.generator.ChunkGeneratorFactory;

public class FlatChunkGeneratorFactory implements ChunkGeneratorFactory {

    @Override
    public ChunkGenerator createChunkGenerator(Server server) {
        return new FlatChunkGenerator();
    }
}
