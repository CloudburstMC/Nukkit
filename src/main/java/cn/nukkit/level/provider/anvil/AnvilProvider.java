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
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@Log4j2
@ParametersAreNonnullByDefault
class AnvilProvider implements LevelProvider {

    public static final int VERSION = 19133;
    private static final RemovalListener<RegionPosition, RegionFile> REMOVAL_LISTENER = new RegionRemovalListener();

    private final String levelId;

    private final Path levelPath;

    private final Path regionsPath;

    private final Executor executor;

    private final LoadingCache<RegionPosition, RegionFile> regionFiles;

    private volatile boolean closed;

    AnvilProvider(String levelId, Path levelsPath, Executor executor) throws IOException {
        this.levelId = levelId;
        this.executor = executor;
        this.levelPath = levelsPath.resolve(levelId);
        this.regionsPath = this.levelPath.resolve("region");

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
    public CompletableFuture<Void> forEachChunk(ChunkBuilder.Factory factory, BiConsumer<Chunk, Throwable> consumer) {
        checkForClosed();

        int workers = 1;
        if (this.executor instanceof ForkJoinPool) {
            workers = ((ForkJoinPool) this.executor).getParallelism();
        }

        log.info("Using {} workers to convert", workers);

        //noinspection unchecked
        List<Path>[] paths = new List[workers];

        for (int i = 0; i < workers; i++) {
            paths[i] = new ArrayList<>();
        }

        int counter = 0;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.regionsPath, "**.mca")) {
            for (Path path : stream) {
                paths[counter++ % workers].add(path);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        CompletableFuture<?>[] futures = new CompletableFuture[workers];

        for (int i = 0; i < workers; i++) {
            final List<Path> pathList = paths[i];

            futures[i] = CompletableFuture.runAsync(() -> {
                try {
                    for (Path path : pathList) {
                        RegionPosition regionPos = RegionPosition.fromPath(path);
                        if (regionPos == null) {
                            continue;
                        }

                        RegionFile regionFile = new RegionFile(path);

                        for (int x = 0; x < 32; x++) {
                            for (int z = 0; z < 32; z++) {
                                if (!regionFile.hasChunk(x, z)) {
                                    continue;
                                }
                                int chunkX = regionPos.x << 5 | x;
                                int chunkZ = regionPos.z << 5 | z;
                                ChunkBuilder builder = factory.create(chunkX, chunkZ);
                                ByteBuf buffer = regionFile.readChunk(x, z);
                                try {
                                    AnvilConverter.convertToNukkit(builder, buffer);
                                } catch (Exception e) {
                                    consumer.accept(null, e);
                                    continue;
                                } finally {
                                    buffer.release();
                                }
                                consumer.accept(builder.build(), null);
                            }
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, this.executor);
        }

        return CompletableFuture.allOf(futures);
    }

    @Override
    public CompletableFuture<LoadState> loadLevelData(LevelData levelData) {
        checkForClosed();

        return CompletableFuture.supplyAsync(() -> {
            try {
                return AnvilDataSerializer.INSTANCE.load(levelData, this.levelPath, this.levelId);
            } catch (IOException e) {
                throw new CompletionException(e);
            }
        }, this.executor);
    }

    @Override
    public CompletableFuture<Void> saveLevelData(LevelData levelData) {
        checkForClosed();
        return CompletableFuture.runAsync(() -> {
            try {
                AnvilDataSerializer.INSTANCE.save(levelData, this.levelPath, this.levelId);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
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
        private static final Pattern PATTERN = Pattern.compile("^r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca$");

        private final int x;
        private final int z;

        static RegionPosition fromChunk(int x, int z) {
            return new RegionPosition(x >> 5, z >> 5);
        }

        @Nullable
        static RegionPosition fromPath(Path regionPath) {
            Matcher matcher = PATTERN.matcher(regionPath.getFileName().toString());
            if (!matcher.matches()) {
                return null;
            }
            int x = Integer.parseInt(matcher.group(1));
            int z = Integer.parseInt(matcher.group(2));
            return new RegionPosition(x, z);
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
