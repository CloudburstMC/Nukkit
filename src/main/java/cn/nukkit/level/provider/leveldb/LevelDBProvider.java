package cn.nukkit.level.provider.leveldb;

import cn.nukkit.level.LevelData;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.chunk.ChunkBuilder;
import cn.nukkit.level.chunk.LockableChunk;
import cn.nukkit.level.provider.LevelProvider;
import cn.nukkit.level.provider.leveldb.serializer.*;
import cn.nukkit.utils.LoadState;
import com.google.common.base.Preconditions;
import lombok.extern.log4j.Log4j2;
import net.daporkchop.ldbjni.LevelDB;
import org.iq80.leveldb.*;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;

@Log4j2
@ParametersAreNonnullByDefault
class LevelDBProvider implements LevelProvider {

    private final String levelId;
    private final Path path;
    private final Executor executor;
    private final DB db;
    private volatile boolean closed;

    LevelDBProvider(String levelId, Path worldPath, Executor executor) throws IOException {
        this.levelId = levelId;
        this.path = worldPath.resolve(levelId);
        this.executor = executor;
        Path dbPath = this.path.resolve("db");
        Files.createDirectories(dbPath);
        Preconditions.checkArgument(Files.isDirectory(dbPath), "db is not a directory");

        Options options = new Options()
                .createIfMissing(true)
                .compressionType(CompressionType.ZLIB_RAW)
                .blockSize(64 * 1024);
        this.db = LevelDB.PROVIDER.open(dbPath.toFile(), options);
    }

    @Override
    public String getLevelId() {
        return levelId;
    }

    @Override
    public CompletableFuture<Chunk> readChunk(ChunkBuilder chunkBuilder) {
        final int x = chunkBuilder.getX();
        final int z = chunkBuilder.getZ();

        return CompletableFuture.supplyAsync(() -> {
            byte[] versionValue = this.db.get(LevelDBKey.VERSION.getKey(x, z));
            if (versionValue == null || versionValue.length != 1) {
                return null;
            }

            byte chunkVersion = versionValue[0];

            if (chunkVersion < 7) {
                chunkBuilder.dirty();
            }

            ChunkSerializers.deserializeChunk(this.db, chunkBuilder, chunkVersion);
            Data2dSerializer.deserialize(this.db, chunkBuilder);

            BlockEntitySerializer.loadBlockEntities(this.db, chunkBuilder);
            EntitySerializer.loadEntities(this.db, chunkBuilder);

            return chunkBuilder.build();
        }, this.executor);
    }

    @Override
    public CompletableFuture<Void> saveChunk(Chunk chunk) {
        final int x = chunk.getX();
        final int z = chunk.getZ();

        return CompletableFuture.supplyAsync(() -> {
            //we clear the dirty flag here instead of in LevelChunkManager in case there are modifications to the chunk between now and the time it was enqueued
            if (!chunk.clearDirty()) {
                //the chunk was not dirty, do nothing
                return null;
            }
            try (WriteBatch batch = this.db.createWriteBatch()) {
                LockableChunk lockableChunk = chunk.readLockable();
                lockableChunk.lock();
                try {
                    ChunkSerializers.serializeChunk(batch, chunk, 7);
                    Data2dSerializer.serialize(batch, chunk);

                    batch.put(LevelDBKey.VERSION.getKey(x, z), new byte[]{7});

                    BlockEntitySerializer.saveBlockEntities(batch, chunk);
                    EntitySerializer.saveEntities(batch, chunk);
                } finally {
                    lockableChunk.unlock();
                }

                this.db.write(batch);
                return null;
            } catch (IOException e) {
                //can't happen
                throw new RuntimeException(e);
            }
        }, this.executor);
    }

    @Override
    public CompletableFuture<Void> forEachChunk(ChunkBuilder.Factory factory, BiConsumer<Chunk, Throwable> consumer) {
        return CompletableFuture.runAsync(() -> {
            DBIterator iterator = this.db.iterator();
            while (iterator.hasNext()) {
                Map.Entry<byte[], byte[]> entry = iterator.next();
                // TODO: 06/01/2020 Add support for iterating chunks
            }
        }, this.executor);
    }

    @Override
    public CompletableFuture<LoadState> loadLevelData(LevelData levelData) {
        checkForClosed();

        return CompletableFuture.supplyAsync(() -> {
            try {
                return LevelDBDataSerializer.INSTANCE.load(levelData, path, levelId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, this.executor);
    }

    @Override
    public CompletableFuture<Void> saveLevelData(LevelData levelData) {
        checkForClosed();
        return CompletableFuture.runAsync(() -> {
            try {
                LevelDBDataSerializer.INSTANCE.save(levelData, path, levelId);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void close() throws IOException {
        this.closed = true;
        this.db.close();
    }

    private void checkForClosed() {
        Preconditions.checkState(!closed, "LevelProvider closed");
    }
}
