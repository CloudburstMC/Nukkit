package cn.nukkit.level.manager;

import cn.nukkit.event.level.ChunkUnloadEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.chunk.ChunkBuilder;
import cn.nukkit.level.generator.function.ChunkGenerateFunction;
import cn.nukkit.level.generator.function.ChunkPopulateFunction;
import cn.nukkit.level.provider.LevelProvider;
import cn.nukkit.utils.Config;
import co.aikar.timings.Timing;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.spotify.futures.CompletableFutures;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.message.ParameterizedMessage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Log4j2
@ParametersAreNonnullByDefault
public final class LevelChunkManager {

    private static final CompletableFuture<Void> COMPLETED_VOID_FUTURE = CompletableFuture.completedFuture(null);

    private final Level level;
    private final LevelProvider provider;
    private final Long2ObjectMap<LoadingChunk> chunks = new Long2ObjectOpenHashMap<>();
    private final Long2LongMap chunkLoadedTimes = new Long2LongOpenHashMap();
    private final Long2LongMap chunkLastAccessTimes = new Long2LongOpenHashMap();
    private final ChunkGenerateFunction chunkGenerateFunction;
    private final ChunkPopulateFunction chunkPopulateFunction;
    private final Executor executor;

    public LevelChunkManager(Level level) {
        this(level, level.getProvider());
    }

    public LevelChunkManager(Level level, LevelProvider provider) {
        this.level = level;
        this.executor = this.level.getServer().getScheduler().getAsyncPool();
        this.provider = provider;
        this.chunkGenerateFunction = new ChunkGenerateFunction(this.level);
        this.chunkPopulateFunction = new ChunkPopulateFunction(this.level);
    }

    private static long chunkKey(int x, int z) {
        return (((long) x) << 32) | (z & 0xffffffffL);
    }

    public boolean isChunkGenerated(int x, int z) {
        Chunk chunk = this.getLoadedChunk(x, z);
        return chunk != null && chunk.isGenerated();
    }

    public boolean isChunkPopulated(int x, int z) {
        Chunk chunk = this.getLoadedChunk(x, z);
        return chunk != null && chunk.isPopulated();
    }

    /**
     * Returns a set of all loaded chunks in this level.
     *
     * @return chunks
     */
    @Nonnull
    public synchronized Set<Chunk> getLoadedChunks() {
        ImmutableSet.Builder<Chunk> chunks = ImmutableSet.builder();
        for (LoadingChunk loadingChunk : this.chunks.values()) {
            Chunk chunk = loadingChunk.getChunk();
            if (chunk != null) {
                chunks.add(chunk);
            }
        }
        return chunks.build();
    }

    public synchronized int getLoadedCount() {
        return this.chunks.size();
    }

    /**
     * Get chunk at specified coordinate if it is already loaded.
     *
     * @param key chunk key
     * @return chunk or null
     */
    @Nullable
    public synchronized Chunk getLoadedChunk(long key) {
        LoadingChunk chunk = this.chunks.get(key);
        return chunk == null ? null : chunk.getChunk();
    }

    /**
     * Get chunk at specified coordinate if it is already loaded.
     *
     * @param x chunk x
     * @param z chunk z
     * @return chunk or null
     */
    @Nullable
    public synchronized Chunk getLoadedChunk(int x, int z) {
        return getLoadedChunk(Chunk.key(x, z));
    }

    /**
     * Get chunk at specified coordinate. This will block the current thread until the chunk is loaded.
     *
     * @param x chunk x
     * @param z chunk z
     * @return chunk
     */
    @Nonnull
    public Chunk getChunk(int x, int z) {
        return this.getChunk(x, z, true);
    }

    @Nonnull
    public Chunk getChunk(int x, int z, boolean generate) {
        Chunk chunk = getLoadedChunk(x, z);
        if (chunk == null) {
            chunk = this.getChunkFuture(x, z, generate).join();
        }
        return chunk;
    }

    /**
     * Get chunk future at specified coordinate.
     *
     * @param x chunk x
     * @param z chunk z
     * @return chunk future
     */
    @Nonnull
    public CompletableFuture<Chunk> getChunkFuture(int x, int z) {
        return this.getChunkFuture(x, z, true);
    }

    @Nonnull
    public CompletableFuture<Chunk> getChunkFuture(int x, int z, boolean generate) {
        return this.getChunkFuture(x, z, generate, generate);
    }

    @Nonnull
    private synchronized CompletableFuture<Chunk> getChunkFuture(int chunkX, int chunkZ, boolean generate,
                                                                 boolean populate) {
        final long chunkKey = Chunk.key(chunkX, chunkZ);
        this.chunkLastAccessTimes.put(chunkKey, System.currentTimeMillis());
        LoadingChunk chunk = this.chunks.computeIfAbsent(chunkKey, key -> new LoadingChunk(key, true));

        if (generate) {
            chunk.generate();
        }

        if (populate) {
            chunk.populate();
        }

        return chunk.getFuture();
    }

    public synchronized boolean isChunkLoaded(long hash) {
        LoadingChunk chunk = this.chunks.get(hash);
        return chunk != null && chunk.getChunk() != null;
    }

    public synchronized boolean isChunkLoaded(int x, int z) {
        return this.isChunkLoaded(chunkKey(x, z));
    }

    public synchronized boolean unloadChunk(long hash) {
        return this.unloadChunk(hash, true, true);
    }

    public boolean unloadChunk(Chunk chunk) {
        return this.unloadChunk(chunk, true);
    }

    public boolean unloadChunk(Chunk chunk, boolean save) {
        return this.unloadChunk(chunk, save, true);
    }

    public boolean unloadChunk(Chunk chunk, boolean save, boolean safe) {
        Preconditions.checkNotNull(chunk, "chunk");
        Preconditions.checkArgument(chunk.getLevel() == this.level,
                "Chunk is not from this level");
        return this.unloadChunk(Chunk.key(chunk.getX(), chunk.getZ()), save, safe);
    }

    public boolean unloadChunk(long chunkKey, boolean save, boolean safe) {
        boolean result = unloadChunk0(chunkKey, save, safe);
        if (result) {
            this.chunks.remove(chunkKey);
        }
        return result;
    }

    private synchronized boolean unloadChunk0(long chunkKey, boolean save, boolean safe) {
        LoadingChunk loadingChunk = this.chunks.get(chunkKey);
        if (loadingChunk == null) {
            return false;
        }
        Chunk chunk = loadingChunk.getChunk();
        if (chunk == null) {
            return false;
        }
        if (!chunk.getLoaders().isEmpty()) {
            return false;
        }


        try (Timing ignored = this.level.timings.doChunkUnload.startTiming()) {
            ChunkUnloadEvent chunkUnloadEvent = new ChunkUnloadEvent(chunk);
            this.level.getServer().getPluginManager().callEvent(chunkUnloadEvent);
            if (chunkUnloadEvent.isCancelled()) {
                return false;
            }

            if (save) {
                this.saveChunk(chunk);
            }

            if (safe && !this.level.getChunkPlayers(chunk.getX(), chunk.getZ()).isEmpty()) {
                return false;
            }

            this.chunkLastAccessTimes.remove(chunkKey);
            this.chunkLoadedTimes.remove(chunkKey);

            chunk.close();
            return true;
        }
    }

    public synchronized CompletableFuture<Void> saveChunks() {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        for (LoadingChunk loadingChunk : this.chunks.values()) {
            Chunk chunk = loadingChunk.getChunk();
            if (chunk != null) {
                futures.add(saveChunk(chunk));
            }
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    public CompletableFuture<Void> saveChunk(Chunk chunk) {
        Preconditions.checkNotNull(chunk, "chunk");
        Preconditions.checkArgument(chunk.getLevel() == this.level,
                "Chunk is not from this ChunkManager's Level");
        if (chunk.isDirty()) {
            return this.provider.saveChunk(chunk).exceptionally(throwable -> {
                log.warn("Unable to save chunk", throwable);
                return null;
            });
        }
        return COMPLETED_VOID_FUTURE;
    }

    @Nonnull
    public synchronized CompletableFuture<Chunk> regenerateChunk(int x, int z) {
        long chunkKey = Chunk.key(x, z);
        long currentTime = System.currentTimeMillis();
        this.chunkLastAccessTimes.put(chunkKey, currentTime);

        LoadingChunk chunk = this.chunks.get(chunkKey);

        if (chunk == null) {
            // No need to load the old chunk
            chunk = new LoadingChunk(chunkKey, false);
            this.chunks.put(chunkKey, chunk);
        } else {
            chunk.clear();
        }

        chunk.generate();
        chunk.populate();

        return chunk.getFuture();
    }

    public synchronized void tick() {
        long time = System.currentTimeMillis();

        // Spawn chunk
        final int spawnX = this.level.getSafeSpawn().getChunkX();
        final int spawnZ = this.level.getSafeSpawn().getChunkZ();
        final int spawnRadius = 4;//server.getConfiguration().getAdvanced().getSpawnChunkRadius();

        Config config = this.level.getServer().getConfig();

        // Do chunk garbage collection
        try (Timing ignored = this.level.timings.doChunkGC.startTiming()) {
            ObjectIterator<Long2ObjectMap.Entry<LoadingChunk>> iterator = this.chunks.long2ObjectEntrySet().iterator();
            while (iterator.hasNext()) {
                Long2ObjectMap.Entry<LoadingChunk> entry = iterator.next();
                long chunkKey = entry.getLongKey();
                LoadingChunk loadingChunk = entry.getValue();
                Chunk chunk = loadingChunk.getChunk();
                if (chunk == null) {
                    continue; // Chunk hasn't loaded
                }

                if (Math.abs(chunk.getX() - spawnX) <= spawnRadius || Math.abs(chunk.getZ() - spawnZ) <= spawnRadius ||
                        !chunk.getLoaders().isEmpty()) {
                    continue; // Spawn protection or is loaded
                }

                long loadedTime = this.chunkLoadedTimes.get(chunkKey);
                if ((time - loadedTime) <= TimeUnit.SECONDS.toMillis(
                        config.getInt("level-settings.chunk-timeout-after-load", 30))) {
                    continue;
                }

                long lastAccessTime = this.chunkLastAccessTimes.get(chunkKey);
                if ((time - lastAccessTime) <= TimeUnit.SECONDS.toMillis(
                        config.getInt("level-settings. chunk-timeout-after-last-access", 120))) {
                    continue;
                }

                if (this.unloadChunk0(chunkKey, true, true)) {
                    iterator.remove();

//                    if (log.isTraceEnabled()) {
//                        log.trace("Cleared chunk ({},{}) from {}", chunk.getX(), chunk.getZ(), level.getId());
//                    }
                }
            }
        }
    }

    @ToString
    private class LoadingChunk {
        private final int x;
        private final int z;
        private CompletableFuture<Chunk> future;
        private volatile boolean generated;
        private volatile boolean populated;

        public LoadingChunk(long key, boolean load) {
            this.x = Chunk.fromKeyX(key);
            this.z = Chunk.fromKeyZ(key);

            if (load) {
                this.future = LevelChunkManager.this.provider.readChunk(new ChunkBuilder(x, z, LevelChunkManager.this.level))
                        .thenApply(chunk -> {
                            if (chunk == null) {
                                return new Chunk(this.x, this.z, LevelChunkManager.this.level);
                            }
                            chunk.init();
                            return chunk;
                        });
                this.future.whenComplete((chunk, throwable) -> {
                    if (throwable != null) {
                        log.warn(new ParameterizedMessage("Unable to load chunk ({}, {}) in level {} ",
                                this.x, this.z, LevelChunkManager.this.level.getId()), throwable);
                        synchronized (LevelChunkManager.this) {
                            LevelChunkManager.this.chunks.remove(key);
                        }
                    } else {
                        long currentTime = System.currentTimeMillis();
                        synchronized (LevelChunkManager.this) {
                            LevelChunkManager.this.chunkLoadedTimes.put(key, currentTime);
                        }
                    }
                });
            } else {
                this.future = CompletableFuture.completedFuture(new Chunk(x, z, LevelChunkManager.this.level));
            }
        }

        public CompletableFuture<Chunk> getFuture() {
            return future;
        }

        @Nullable
        private Chunk getChunk() {
            return future.getNow(null);
        }

        private void generate() {
            if (generated) {
                Chunk chunk = getChunk();
                if (chunk == null || chunk.isGenerated()) {
                    return;
                }
            }
            generated = true;

            future = future.thenApplyAsync(LevelChunkManager.this.chunkGenerateFunction, LevelChunkManager.this.executor);
        }

        private void populate() {
            this.generate(); // Generation has to happen before population

            if (populated) {
                Chunk chunk = getChunk();
                if (chunk == null || chunk.isPopulated()) {
                    return;
                }
            }
            populated = true;

            // Load and generate chunks around the chunk to be populated.
            List<CompletableFuture<Chunk>> chunksToLoad = new ArrayList<>(8);
            for (int z = this.z - 1, maxZ = this.z + 1; z <= maxZ; z++) {
                for (int x = this.x - 1, maxX = this.x + 1; x <= maxX; x++) {
                    if (x == this.x && z == this.z) continue;
                    chunksToLoad.add(LevelChunkManager.this.getChunkFuture(x, z, true, false));
                }
            }
            CompletableFuture<List<Chunk>> aroundFuture = CompletableFutures.allAsList(chunksToLoad);

            future = future.thenCombineAsync(aroundFuture, LevelChunkManager.this.chunkPopulateFunction,
                    LevelChunkManager.this.executor);
        }

        private void clear() {
            this.generated = false;
            this.populated = false;
            this.future = future.thenApply(chunk -> {
                chunk.clear();
                return chunk;
            });
        }
    }
}
