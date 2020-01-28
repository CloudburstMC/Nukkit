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

    private final Level         level;
    private final LevelProvider provider;
    private final Long2ObjectMap<LoadingChunk> chunks               = new Long2ObjectOpenHashMap<>();
    private final Long2LongMap                 chunkLoadedTimes     = new Long2LongOpenHashMap();
    private final Long2LongMap                 chunkLastAccessTimes = new Long2LongOpenHashMap();
    private final ChunkGenerateFunction chunkGenerateFunction;
    private final ChunkPopulateFunction chunkPopulateFunction;
    private final Executor              executor;

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
            CompletableFuture<Chunk> chunkFuture = loadingChunk.populateAround(true);
            Chunk chunk;
            if (chunkFuture != null && (chunk = chunkFuture.getNow(null)) != null) {
                chunks.add(chunk);
            }
        }
        return chunks.build();
    }

    public int getLoadedCount() {
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
        LoadingChunk loadingChunk = this.chunks.get(key);
        if (loadingChunk != null) {
            CompletableFuture<Chunk> chunkFuture = loadingChunk.populateAround(true);
            if (chunkFuture != null) {
                return chunkFuture.getNow(null);
            }
        }
        return null;
    }

    /**
     * Get chunk at specified coordinate if it is already loaded.
     *
     * @param x chunk x
     * @param z chunk z
     * @return chunk or null
     */
    @Nullable
    public Chunk getLoadedChunk(int x, int z) {
        return this.getLoadedChunk(Chunk.key(x, z));
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
        Chunk chunk = this.getLoadedChunk(x, z);
        if (chunk == null) {
            chunk = this.getChunkFuture(x, z).join();
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
        return this.getChunkFuture(x, z, true, true, true);
    }

    /**
     * Get chunk future at the specified coordinates without forcing the chunk to be generated.
     * <p>
     * This is unsafe, and should not be used without good reason.
     *
     * @param x chunk x
     * @param z chunk z
     * @return chunk future
     */
    @Nonnull
    public CompletableFuture<Chunk> getLoadedChunkFutureUnsafe(int x, int z) {
        return this.getChunkFuture(x, z, false, false, false);
    }

    /**
     * Get chunk future at the specified coordinates without forcing the chunk to be populated.
     * <p>
     * This is unsafe, and should not be used without good reason.
     *
     * @param x chunk x
     * @param z chunk z
     * @return chunk future
     */
    @Nonnull
    public CompletableFuture<Chunk> getGeneratedChunkFutureUnsafe(int x, int z) {
        return this.getChunkFuture(x, z, true, false, false);
    }

    /**
     * Get chunk future at the specified coordinates without forcing the chunk's population chunks be populated.
     * <p>
     * This is unsafe, and should not be used without good reason.
     *
     * @param x chunk x
     * @param z chunk z
     * @return chunk future
     */
    @Nonnull
    public CompletableFuture<Chunk> getPopulatedChunkFutureUnsafe(int x, int z) {
        return this.getChunkFuture(x, z, true, true, false);
    }

    @Nonnull
    private synchronized CompletableFuture<Chunk> getChunkFuture(int chunkX, int chunkZ, boolean generate, boolean populate, boolean populateAround) {
        final long chunkKey = Chunk.key(chunkX, chunkZ);
        this.chunkLastAccessTimes.put(chunkKey, System.currentTimeMillis());
        LoadingChunk chunk = this.chunks.computeIfAbsent(chunkKey, key -> new LoadingChunk(key, true));

        if (populateAround) {
            return chunk.populateAround(false);
        } else if (populate)    {
            return chunk.populate(false);
        } else if (generate)    {
            return chunk.generate(false);
        } else {
            return chunk.load();
        }
    }

    public synchronized boolean isChunkLoaded(long hash) {
        LoadingChunk loadingChunk = this.chunks.get(hash);
        if (loadingChunk != null) {
            CompletableFuture<Chunk> chunkFuture = loadingChunk.populateAround(true);
            Chunk chunk;
            return chunkFuture != null && (chunk = chunkFuture.getNow(null)) != null;
        } else {
            return false;
        }
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
        CompletableFuture<Chunk> chunkFuture = loadingChunk.bestFuture();
        if (chunkFuture == null)  {
            return false;
        }
        Chunk chunk = chunkFuture.getNow(null);
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
            CompletableFuture<Chunk> chunkFuture = loadingChunk.populateAround(true);
            Chunk chunk;
            if (chunkFuture != null && (chunk = chunkFuture.getNow(null)) != null) {
                futures.add(this.saveChunk(chunk));
            }
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    public CompletableFuture<Void> saveChunk(Chunk chunk) {
        Preconditions.checkNotNull(chunk, "chunk");
        Preconditions.checkArgument(chunk.getLevel() == this.level,
                "Chunk is not from this ChunkManager's Level");
        if (chunk.isDirty()) {
            chunk.setDirty(false);
            return this.provider.saveChunk(chunk).exceptionally(throwable -> {
                log.warn("Unable to save chunk", throwable);
                return null;
            });
        }
        return COMPLETED_VOID_FUTURE;
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

                CompletableFuture<Chunk> chunkFuture = loadingChunk.bestFuture();
                Chunk chunk;
                if (chunkFuture == null || (chunk = chunkFuture.getNow(null)) == null) {
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
        private final CompletableFuture<Chunk> loadFuture;
        private       CompletableFuture<Chunk> generateFuture;
        private       CompletableFuture<Chunk> populateFuture;
        private       CompletableFuture<Chunk> populateAroundFuture;
        private final int                      x;
        private final int                      z;

        public LoadingChunk(long key, boolean load) {
            this.x = Chunk.fromKeyX(key);
            this.z = Chunk.fromKeyZ(key);

            if (load) {
                this.loadFuture = LevelChunkManager.this.provider.readChunk(new ChunkBuilder(x, z, LevelChunkManager.this.level))
                        .thenApply(chunk -> {
                            if (chunk == null) {
                                return new Chunk(this.x, this.z, LevelChunkManager.this.level);
                            }
                            chunk.init();
                            return chunk;
                        });
                this.loadFuture.whenComplete((chunk, throwable) -> {
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
                this.loadFuture = CompletableFuture.completedFuture(new Chunk(x, z, LevelChunkManager.this.level));
            }
        }

        private CompletableFuture<Chunk> load() {
            return this.loadFuture;
        }

        private CompletableFuture<Chunk> generate(boolean skipCreate) {
            if (skipCreate || this.generateFuture != null) {
                return this.generateFuture;
            }

            return this.generateFuture = this.load()
                    .thenApplyAsync(LevelChunkManager.this.chunkGenerateFunction, LevelChunkManager.this.executor);
        }

        private CompletableFuture<Chunk> populate(boolean skipCreate) {
            if (skipCreate || this.populateFuture != null) {
                return this.populateFuture;
            }

            CompletableFuture<Chunk> generateFuture = this.generate(false); // Generation has to happen before population

            // Load and generate chunks around the chunk to be populated.
            List<CompletableFuture<Chunk>> chunksToLoad = new ArrayList<>(8);
            for (int z = this.z - 1, maxZ = this.z + 1; z <= maxZ; z++) {
                for (int x = this.x - 1, maxX = this.x + 1; x <= maxX; x++) {
                    if (x == this.x && z == this.z) continue;
                    chunksToLoad.add(LevelChunkManager.this.getChunkFuture(x, z, true, false, false));
                }
            }
            CompletableFuture<List<Chunk>> aroundFuture = CompletableFutures.allAsList(chunksToLoad);

            return this.populateFuture = generateFuture
                    .thenCombineAsync(aroundFuture, LevelChunkManager.this.chunkPopulateFunction, LevelChunkManager.this.executor);
        }

        private CompletableFuture<Chunk> populateAround(boolean skipCreate) {
            if (skipCreate || this.populateAroundFuture != null) {
                return this.populateAroundFuture;
            }

            // Load and generate chunks around the chunk to be populated.
            List<CompletableFuture<Chunk>> chunksToLoad = new ArrayList<>(9);
            chunksToLoad.add(this.populate(false));
            for (int z = this.z - 1, maxZ = this.z + 1; z <= maxZ; z++) {
                for (int x = this.x - 1, maxX = this.x + 1; x <= maxX; x++) {
                    if (x == this.x && z == this.z) {
                        continue;
                    }
                    chunksToLoad.add(LevelChunkManager.this.getChunkFuture(x, z, true, true, false));
                }
            }

            return this.populateAroundFuture = CompletableFutures.allAsList(chunksToLoad)
                    .thenApply(chunks -> {
                        Chunk chunk = chunks.get(0);
                        if (!chunk.isPopulatedAround()) {
                            throw new IllegalStateException("Chunk was not populated around!");
                        }
                        return chunk;
                    });
        }

        private CompletableFuture<Chunk> bestFuture() {
            if (this.populateAroundFuture != null) {
                return this.populateAroundFuture;
            } else if (this.populateFuture != null) {
                return this.populateFuture;
            } else if (this.generateFuture != null) {
                return this.generateFuture;
            } else {
                return this.loadFuture;
            }
        }
    }
}
