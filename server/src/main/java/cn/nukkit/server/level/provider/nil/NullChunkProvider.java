package cn.nukkit.server.level.provider.nil;

import cn.nukkit.api.level.Level;
import cn.nukkit.api.level.chunk.Chunk;
import cn.nukkit.server.level.provider.ChunkProvider;

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
