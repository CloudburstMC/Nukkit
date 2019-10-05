package cn.nukkit.level.provider;

import cn.nukkit.level.LevelData;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.chunk.ChunkBuilder;
import cn.nukkit.utils.LoadState;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;

/**
 * Interface that allows the level to load and save chunks from any storage implementation.
 */
@ParametersAreNonnullByDefault
public interface LevelProvider extends PlayerDataProvider, Closeable {

    String getLevelId();

    /**
     * @param chunkBuilder builder
     * @return future when chunk is loaded. Will return null if the chunk does not exist
     */
    CompletableFuture<Chunk> readChunk(ChunkBuilder chunkBuilder);

    CompletableFuture<Void> saveChunk(Chunk chunk);

    void forEachChunk(BiConsumer<Chunk, Throwable> consumer);

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

    @FunctionalInterface
    interface Factory {

        /**
         * @param levelId   level ID
         * @param worldPath path to worlds directory
         * @param executor  executor to run tasks async
         * @return chunk provider
         * @throws IOException
         */
        LevelProvider create(String levelId, Path worldPath, Executor executor) throws IOException;
    }
}
