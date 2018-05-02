package com.nukkitx.server.level.provider.nil;

import com.nukkitx.api.level.Level;
import com.nukkitx.api.level.chunk.Chunk;
import com.nukkitx.server.level.provider.ChunkProvider;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class NullChunkProvider implements ChunkProvider {
    @Override
    public CompletableFuture<Chunk> createChunk(Level level, int x, int z, Executor executor) {
        // Do nothing
        CompletableFuture<Chunk> chunkFuture = new CompletableFuture<>();
        chunkFuture.complete(null);
        return chunkFuture;
    }

    @Override
    public void saveChunk(Chunk chunk, Executor executor) {
        // Do nothing
    }
}
