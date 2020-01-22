package cn.nukkit.level.provider;

import cn.nukkit.level.LevelData;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.chunk.ChunkBuilder;
import cn.nukkit.utils.LoadState;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.Closeable;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * Interface that allows the level to load and save chunks from any storage implementation.
 */
@ParametersAreNonnullByDefault
public interface LevelProvider extends PlayerDataProvider, Closeable {

    /**
     * Level ID
     *
     * @return id
     */
    String getLevelId();

    /**
     * Reads chunk from provider asynchronously
     *
     * @param chunkBuilder builder
     * @return future when chunk is loaded. Will return null if the chunk does not exist
     */
    CompletableFuture<Chunk> readChunk(ChunkBuilder chunkBuilder);

    /**
     * Saves chunk to provider asynchronously
     *
     * @param chunk chunk
     * @return void future when chunk is saved.
     */
    CompletableFuture<Void> saveChunk(Chunk chunk);

    /**
     * Iterate over all chunks that the provider has.
     *
     * @param consumer
     * @throws UnsupportedOperationException if the provider does not support chunk iteration.
     */
    CompletableFuture<Void> forEachChunk(ChunkBuilder.Factory factory, BiConsumer<Chunk, Throwable> consumer);

    /**
     * Load level data into given {@link LevelData} object
     *
     * @param levelData levelData to load
     * @return future of loaded level data
     */
    CompletableFuture<LoadState> loadLevelData(LevelData levelData);

    /**
     * Save level data from given {@link LevelData} object
     *
     * @param levelData levelData to save
     */
    CompletableFuture<Void> saveLevelData(LevelData levelData);
}
