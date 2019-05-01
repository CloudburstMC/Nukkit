package com.nukkitx.server.level.manager;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.nukkitx.api.entity.Entity;
import com.nukkitx.api.event.level.ChunkUnloadEvent;
import com.nukkitx.api.level.chunk.Chunk;
import com.nukkitx.api.level.chunk.generator.ChunkGenerator;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.level.NukkitLevel;
import com.nukkitx.server.level.chunk.SectionedChunk;
import com.nukkitx.server.level.provider.ChunkProvider;
import com.nukkitx.server.network.bedrock.session.PlayerSession;
import com.spotify.futures.CompletableFutures;
import gnu.trove.TCollections;
import gnu.trove.map.TLongLongMap;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongLongHashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

@Log4j2
public class LevelChunkManager {
    private final TLongObjectMap<Chunk> chunksLoaded = TCollections.synchronizedMap(new TLongObjectHashMap<>());
    private final TLongObjectMap<LoadingTask> chunksToLoad = TCollections.synchronizedMap(new TLongObjectHashMap<>());
    private final TLongLongMap loadedTimes = TCollections.synchronizedMap(new TLongLongHashMap());
    private final TLongLongMap lastAccessTimes = TCollections.synchronizedMap(new TLongLongHashMap());
    private final ExecutorService chunkLoadingService;
    private final ChunkProvider chunkProvider;
    private final ChunkGenerator chunkGenerator;
    private final NukkitServer server;
    private final NukkitLevel level;

    public LevelChunkManager(NukkitServer server, NukkitLevel level, ChunkProvider chunkProvider, ChunkGenerator chunkGenerator) {
        this.server = server;
        this.level = level;
        this.chunkProvider = chunkProvider;
        this.chunkGenerator = chunkGenerator;
        this.chunkLoadingService = Executors.newFixedThreadPool(Math.max(1, server.getConfiguration().getAdvanced().getChunkLoadThreads()),
                new ThreadFactoryBuilder()
                        .setNameFormat("Chunk handler for " + level.getId() + " - #%d")
                        .setDaemon(true)
                        .build()
        );
    }

    public CompletableFuture<Chunk> getChunk(int x, int z) {
        // Check if chunk is already loaded.
        Optional<Chunk> alreadyLoaded = getChunkIfLoaded(x, z);
        if (alreadyLoaded.isPresent()) {
            return CompletableFuture.completedFuture(alreadyLoaded.get());
        }

        long chunkKey = getChunkKey(x, z);
        LoadingTask loadingTask = chunksToLoad.get(chunkKey);
        if (loadingTask == null) {
            loadingTask = new LoadingTask(x, z);
            chunksToLoad.put(chunkKey, loadingTask);
        }

        CompletableFuture<Chunk> resultFuture = loadingTask.createCompletableFuture();
        if (!loadingTask.hasStarted()) {
            loadingTask.execute();
        }

        return resultFuture;
    }

    public Optional<Chunk> getChunkIfLoaded(int x, int z) {
        long chunkKey = getChunkKey(x, z);
        Chunk chunk = chunksLoaded.get(chunkKey);
        if (chunk != null) {
            lastAccessTimes.put(chunkKey, System.currentTimeMillis());
        }

        return Optional.ofNullable(chunk);
    }

    public void onTick() {
        long time = System.currentTimeMillis();

        // Spawn chunk
        final int spawnX = level.getData().getDefaultSpawn().getFloorX() >> 4;
        final int spawnZ = level.getData().getDefaultSpawn().getFloorZ() >> 4;
        final int spawnRadius = server.getConfiguration().getAdvanced().getSpawnChunkRadius();

        // Do chunk garbage collection
        if (server.getConfiguration().getAdvanced().isChunkGCEnabled()) {
            chunkGC:
            for (long chunkKey : chunksLoaded.keys()) {
                int x = (int) (chunkKey >> 32);
                int z = (int) chunkKey + Integer.MIN_VALUE;

                if (Math.abs(x - spawnX) <= spawnRadius || Math.abs(z - spawnZ) <= spawnRadius) {
                    continue; // Already loaded
                }

                long loadedTime = loadedTimes.get(chunkKey);
                if ((loadedTime - time) <= TimeUnit.SECONDS.toMillis(server.getConfiguration().getAdvanced().getChunkTimeoutAfterLoad())) {
                    continue;
                }

                long lastAccessTime = lastAccessTimes.get(chunkKey);
                if ((lastAccessTime - time) <= TimeUnit.SECONDS.toMillis(server.getConfiguration().getAdvanced().getChunkTimeoutAfterLastAccess())) {
                    continue;
                }

                for (PlayerSession session : level.getEntityManager().getPlayers()) {
                    if (session.isChunkInView(x, z)) {
                        continue chunkGC;
                    }
                }

                ChunkUnloadEvent event = new ChunkUnloadEvent(chunksLoaded.get(chunkKey));
                server.getEventManager().fire(event);
                if (event.isCancelled()) {
                    lastAccessTimes.put(chunkKey, System.currentTimeMillis());
                    continue;
                }

                // TODO: Save chunk before garbage collection.

                chunksLoaded.remove(chunkKey);
                lastAccessTimes.remove(chunkKey);
                loadedTimes.remove(chunkKey);
                level.getEntityManager().getEntitiesInChunk(x, z).forEach(Entity::remove);

                if (log.isTraceEnabled()) {
                    log.trace("Cleared chunk ({},{}) from {}", x, z, level.getId());
                }
            }
        }
    }

    private class LoadingTask {
        private final int x;
        private final int z;
        private final List<CompletableFuture<Chunk>> chunkFuturesToComplete = new CopyOnWriteArrayList<>();
        private final AtomicReference<LoadState> loadState = new AtomicReference<>();
        private final AtomicReference<Chunk> loaded = new AtomicReference<>();
        private final AtomicReference<Throwable> loadException = new AtomicReference<>();

        private LoadingTask(int x, int z) {
            this.x = x;
            this.z = z;
        }

        boolean hasStarted() {
            return loadState.get() != null;
        }

        void execute() {
            if (!loadState.compareAndSet(null, LoadState.STARTED)) return;

            final long chunkKey = getChunkKey(x, z);
            chunkProvider.createChunk(level, x, z, chunkLoadingService).thenAcceptAsync(chunk -> {
                // No chunk found so we need to generate it.
                if (chunk == null) {
                    long seed = getChunkSeed(x, z);
                    if (log.isTraceEnabled()) {
                        log.trace("Generating chunk {},{} using {} with seed {}", x, z, chunkGenerator.getClass().getName(), seed);
                    }
                    SectionedChunk newChunk = new SectionedChunk(x, z, level);
                    chunkGenerator.generateChunk(level, newChunk, new Random(seed));
                    newChunk.recalculateLight();
                    chunk = newChunk;
                }

                chunksLoaded.put(chunkKey, chunk);
                long time = System.currentTimeMillis();
                loadedTimes.put(chunkKey, time);
                lastAccessTimes.put(chunkKey, time);
                loaded.set(chunk);
                loadState.set(LoadState.COMPLETED);

                for (CompletableFuture<Chunk> future : chunkFuturesToComplete) {
                    future.complete(chunk);
                }
            }, chunkLoadingService).exceptionally(throwable -> {
                loadException.set(throwable);
                loadState.set(LoadState.ERRED);
                chunksToLoad.remove(chunkKey);

                for (CompletableFuture<Chunk> future : chunkFuturesToComplete) {
                    future.completeExceptionally(throwable);
                }
                return null;
            });
        }

        CompletableFuture<Chunk> createCompletableFuture() {
            LoadState currentState = loadState.get();
            if (currentState == LoadState.COMPLETED) {
                return CompletableFuture.completedFuture(loaded.get());
            } else if (currentState == LoadState.ERRED) {
                return CompletableFutures.exceptionallyCompletedFuture(loadException.get());
            }

            CompletableFuture<Chunk> future = new CompletableFuture<>();
            chunkFuturesToComplete.add(future);
            return future;
        }
    }

    private enum LoadState {
        STARTED,
        COMPLETED,
        ERRED
    }

    private long getChunkSeed(int x, int z) {
        return getChunkKey(x, z) ^ level.getData().getRandomSeed();
    }

    // Combine chunk's x and z to form a key for reference.
    private static long getChunkKey(int x, int z) {
        return ((long) x << 32) + z - Integer.MIN_VALUE;
    }
}
