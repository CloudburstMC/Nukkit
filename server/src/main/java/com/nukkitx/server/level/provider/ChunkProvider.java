package com.nukkitx.server.level.provider;

import com.nukkitx.api.level.Level;
import com.nukkitx.api.level.chunk.Chunk;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public interface ChunkProvider {

    CompletableFuture<Chunk> createChunk(Level level, int x, int z, Executor executor);

    void saveChunk(Chunk chunk, Executor executor);
}
