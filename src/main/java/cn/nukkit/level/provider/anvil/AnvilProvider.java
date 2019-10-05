package cn.nukkit.level.provider.anvil;

import cn.nukkit.level.LevelData;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.chunk.ChunkBuilder;
import cn.nukkit.level.provider.LevelProvider;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.LoadState;
import com.google.common.base.Preconditions;
import com.google.common.cache.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufOutputStream;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ParametersAreNonnullByDefault
public class AnvilProvider implements LevelProvider {

    public static final int VERSION = 19133;
    private static final RemovalListener<RegionPosition, RegionFile> REMOVAL_LISTENER = new RegionRemovalListener();
    public static final Factory FACTORY = AnvilProvider::new;
    private final String levelId;

    private final Path dataPath;

    private final Path regionsPath;

    private final Executor executor;

    private final LoadingCache<RegionPosition, RegionFile> regionFiles;

    private volatile boolean closed;

    private AnvilProvider(String levelId, Path levelsPath, Executor executor) throws IOException {
        this.levelId = levelId;
        this.executor = executor;
        Path levelPath = levelsPath.resolve(levelId);
        this.dataPath = levelPath.resolve("level.dat");
        this.regionsPath = levelPath.resolve("region");

        // Check for valid files
        Files.createDirectories(regionsPath);
        Preconditions.checkArgument(Files.isDirectory(regionsPath), "region is not a directory");

        this.regionFiles = CacheBuilder.newBuilder()
                .expireAfterAccess(2, TimeUnit.MINUTES)
                .removalListener(REMOVAL_LISTENER)
                .build(CacheLoader.from(regionPosition -> {
                    try {
                        return new RegionFile(regionsPath.resolve("r." + regionPosition.x + '.' + regionPosition.z + ".mca"));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }));
    }

    @Override
    public String getLevelId() {
        return levelId;
    }

    @Override
    public CompletableFuture<Chunk> readChunk(ChunkBuilder chunkBuilder) {
        checkForClosed();
        CompletableFuture<Chunk> future = new CompletableFuture<>();

        final int x = chunkBuilder.getX();
        final int z = chunkBuilder.getZ();

        final RegionPosition regionPosition = RegionPosition.fromChunk(x, z);
        final int inX = x & 0x1f;
        final int inZ = z & 0x1f;

        this.executor.execute(() -> {
            try {
                Path regionPath = this.regionsPath.resolve(regionPosition.getFileName());
                if (Files.notExists(regionPath)) {
                    future.complete(null);
                    return;
                }
                RegionFile file = this.regionFiles.get(regionPosition);

                if (!file.hasChunk(inX, inZ)) {
                    future.complete(null);
                    return;
                }

                ByteBuf chunkBuf = file.readChunk(inX, inZ);
                try {
                    AnvilConverter.convertToNukkit(chunkBuilder, chunkBuf);

                    future.complete(chunkBuilder.build());
                } finally {
                    chunkBuf.release();
                }
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    @Override
    public CompletableFuture<Void> saveChunk(Chunk chunk) {
        checkForClosed();

        CompletableFuture<Void> savedFuture = new CompletableFuture<>();

        final int x = chunk.getX();
        final int z = chunk.getZ();

        final RegionPosition regionPosition = RegionPosition.fromChunk(x, z);
        final int inX = x & 0x1f;
        final int inZ = z & 0x1f;

        this.executor.execute(() -> {
            try {
                CompoundTag tag = AnvilConverter.convertToAnvil(chunk);

                RegionFile file = this.regionFiles.get(regionPosition);

                ByteBuf buffer = ByteBufAllocator.DEFAULT.ioBuffer();
                try (ByteBufOutputStream stream = new ByteBufOutputStream(buffer)) {
                    NBTIO.write(tag, stream, ByteOrder.BIG_ENDIAN);
                    file.writeChunk(inX, inZ, buffer);
                } finally {
                    buffer.release();
                }
            } catch (Exception e) {
                savedFuture.completeExceptionally(e);
            }
        });

        return savedFuture;
    }

    @Override
    public void forEachChunk(BiConsumer<Chunk, Throwable> consumer) {
        checkForClosed();

        this.executor.execute(() -> {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.regionsPath, "*.mca")) {
                for (Path path : stream) {
                    RegionPosition position = RegionPosition.fromPath(path);
                    if (position == null) {
                        continue;
                    }

                    RegionFile regionFile = new RegionFile(path);

                    for (int x = 0; x < 32; x++) {
                        for (int z = 0; z < 32; z++) {
                            if (!regionFile.hasChunk(x, z)) {
                                continue;
                            }
                            int chunkX = position.x << 5 | x;
                            int chunkZ = position.z << 5 | z;
                            ChunkBuilder builder = new ChunkBuilder(chunkX, chunkZ);
                            try {
                                AnvilConverter.convertToNukkit(builder, regionFile.readChunk(x, z));
                            } catch (Exception e) {
                                consumer.accept(null, e);
                                continue;
                            }
                            consumer.accept(builder.build(), null);
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public CompletableFuture<LoadState> loadLevelData(LevelData levelData) {
        checkForClosed();

        return CompletableFuture.supplyAsync(() -> {
            try {
                if (Files.notExists(this.dataPath)) {
                    return LoadState.NOT_FOUND;
                }

                CompoundTag tag;
                try (InputStream stream = Files.newInputStream(this.dataPath)) {
                    tag = NBTIO.readCompressed(stream, ByteOrder.BIG_ENDIAN);
                }

                return LoadState.LOADED;
            } catch (IOException e) {
                throw new CompletionException(e);
            }
        }, this.executor);
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
        this.regionFiles.invalidateAll();
    }

    private void checkForClosed() {
        Preconditions.checkState(!closed, "LevelProvider closed");
    }

    @RequiredArgsConstructor
    private static class RegionPosition {
        private static final Pattern PATTERN = Pattern.compile("^r\\.([0-9]+)\\.([0-9]+)\\.mca$");

        private final int x;
        private final int z;

        static RegionPosition fromChunk(int x, int z) {
            return new RegionPosition(x >> 5, z >> 5);
        }

        @Nullable
        static RegionPosition fromPath(Path regionPath) {
            Matcher matcher = PATTERN.matcher(regionPath.getFileName().toString());
            if (!matcher.find()) {
                return null;
            }
            int x = Integer.parseInt(matcher.group(1));
            int z = Integer.parseInt(matcher.group(2));
            return fromChunk(x, z);
        }

        String getFileName() {
            return "r." + this.x + '.' + this.z + ".mca";
        }
    }

    private static class RegionRemovalListener implements RemovalListener<RegionPosition, RegionFile> {

        @Override
        public void onRemoval(RemovalNotification<RegionPosition, RegionFile> notification) {
            try {
                notification.getValue().close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
