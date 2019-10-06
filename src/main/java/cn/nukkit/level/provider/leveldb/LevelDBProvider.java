package cn.nukkit.level.provider.leveldb;

import cn.nukkit.level.LevelData;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.chunk.ChunkBuilder;
import cn.nukkit.level.provider.LevelProvider;
import cn.nukkit.level.provider.leveldb.serializer.ChunkSerializers;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.LoadState;
import com.google.common.base.Preconditions;
import lombok.extern.log4j.Log4j2;
import org.iq80.leveldb.CompressionType;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;

@Log4j2
@ParametersAreNonnullByDefault
public class LevelDBProvider implements LevelProvider {

    public static final LevelProvider.Factory FACTORY = LevelDBProvider::new;

    private final String levelId;
    private final Path path;
    private final Executor executor;
    private final DB db;
    private volatile boolean closed;

    private LevelDBProvider(String levelId, Path worldPath, Executor executor) throws IOException {
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
        this.db = Iq80DBFactory.factory.open(dbPath.toFile(), options);
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

            return chunkBuilder.build();
        }, this.executor);
    }

    @Override
    public CompletableFuture<Void> saveChunk(Chunk chunk) {
        final int x = chunk.getX();
        final int z = chunk.getZ();

        return CompletableFuture.supplyAsync(() -> {
            this.db.put(LevelDBKey.VERSION.getKey(x, z), new byte[]{3});

            ChunkSerializers.serializeChunk(this.db, chunk, 3);

            return null;
        }, this.executor);
    }

    @Override
    public void forEachChunk(BiConsumer<Chunk, Throwable> consumer) {
        executor.execute(() -> {
            this.db.forEach(entry -> {

            });
        });
    }

    @Override
    public CompletableFuture<LoadState> loadLevelData(LevelData levelData) {
        checkForClosed();
        CompletableFuture<LoadState> loadedFuture = new CompletableFuture<>();

        this.executor.execute(() -> {
            try {
                Path dataPath = this.path.resolve("level.dat");

                if (Files.notExists(dataPath)) {
                    loadedFuture.complete(null);
                    return;
                }

                CompoundTag tag;
                try (InputStream stream = Files.newInputStream(dataPath)) {
                    if (stream.skip(8) != 8) {
                        throw new IllegalArgumentException("Corrupt level.dat size");
                    }
                    tag = NBTIO.read(stream, ByteOrder.LITTLE_ENDIAN, true);
                }

                loadedFuture.complete(LoadState.LOADED);
            } catch (Exception e) {
                loadedFuture.completeExceptionally(e);
            }
        });

        return loadedFuture;
    }

    @Override
    public CompletableFuture<Void> saveLevelData(LevelData levelData) {
        checkForClosed();
        CompletableFuture<Void> savedFuture = new CompletableFuture<>();
        savedFuture.complete(null);
        return savedFuture;
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
