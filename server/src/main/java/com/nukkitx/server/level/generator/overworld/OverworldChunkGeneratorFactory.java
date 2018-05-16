package com.nukkitx.server.level.generator.overworld;

import com.nukkitx.api.Server;
import com.nukkitx.api.level.chunk.generator.ChunkGenerator;
import com.nukkitx.api.level.chunk.generator.ChunkGeneratorFactory;

/**
 * @author DaPorkchop_
 */
public class OverworldChunkGeneratorFactory implements ChunkGeneratorFactory {
    @Override
    public ChunkGenerator createChunkGenerator(Server server) {
        return new OverworldChunkGenerator();
    }
}
