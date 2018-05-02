package com.nukkitx.server.level.generator;

import com.nukkitx.api.Server;
import com.nukkitx.api.level.chunk.generator.ChunkGenerator;
import com.nukkitx.api.level.chunk.generator.ChunkGeneratorFactory;

public class FlatChunkGeneratorFactory implements ChunkGeneratorFactory {

    @Override
    public ChunkGenerator createChunkGenerator(Server server) {
        return new FlatChunkGenerator();
    }
}
