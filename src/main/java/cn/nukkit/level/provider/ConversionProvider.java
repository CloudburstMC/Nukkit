package cn.nukkit.level.provider;

import cn.nukkit.level.LevelData;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.chunk.ChunkBuilder;
import cn.nukkit.utils.LoadState;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@ParametersAreNonnullByDefault
public class ConversionProvider implements LevelProvider {
    private final LevelProvider newChunkProvider;
    private final LevelProvider oldChunkProvider;

    public ConversionProvider(LevelProvider newChunkProvider, LevelProvider oldChunkProvider) {
        this.newChunkProvider = newChunkProvider;
        this.oldChunkProvider = oldChunkProvider;
    }

    @Override
    public String getLevelId() {
        return newChunkProvider.getLevelId();
    }

    @Override
    public CompletableFuture<Chunk> readChunk(ChunkBuilder chunkBuilder) {
        return this.newChunkProvider.readChunk(chunkBuilder).thenCompose(chunk -> {
            if (chunk == null) {
                // Couldn't find chunk in new provider so lets check the old one
                return this.oldChunkProvider.readChunk(chunkBuilder).thenApply(oldChunk -> {
                    // This chunk must be saved with the new provider.
                    if (oldChunk != null) {
                        oldChunk.setDirty();
                    }
                    return oldChunk;
                });
            }
            return CompletableFuture.completedFuture(chunk);
        });
    }

    @Override
    public CompletableFuture<Void> saveChunk(Chunk chunk) {
        return this.newChunkProvider.saveChunk(chunk);
    }

    @Override
    public void forEachChunk(BiConsumer<Chunk, Throwable> consumer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<LoadState> loadLevelData(LevelData levelData) {
        return this.newChunkProvider.loadLevelData(levelData).thenCompose(loadState -> {
            if (loadState == LoadState.NOT_FOUND) {
                return this.oldChunkProvider.loadLevelData(levelData);
            }
            return CompletableFuture.completedFuture(loadState);
        });
    }

    @Override
    public CompletableFuture<Void> saveLevelData(LevelData levelData) {
        return this.newChunkProvider.saveLevelData(levelData);
    }

    @Override
    public void close() throws IOException {
        this.newChunkProvider.close();
        this.oldChunkProvider.close();
    }
}
