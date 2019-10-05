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
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.message.ParameterizedMessage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Log4j2
@ParametersAreNonnullByDefault
public final class LevelChunkManager {

    private final Level level;
    private final LevelProvider provider;
    private final Long2ObjectMap<Chunk> chunksLoaded = new Long2ObjectOpenHashMap<>();
    private final Long2ObjectMap<CompletableFuture<Chunk>> chunkFutures = new Long2ObjectOpenHashMap<>();
    private final Long2LongMap chunkLoadedTimes = new Long2LongOpenHashMap();
    private final Long2LongMap chunkLastAccessTimes = new Long2LongOpenHashMap();
    private final ChunkGenerateFunction chunkGenerateFunction;
    private final ChunkPopulateFunction chunkPopulateFunction;

    public LevelChunkManager(Level level) {
        this(level, level.getProvider());
    }

    public LevelChunkManager(Level level, LevelProvider provider) {
        this.level = level;
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
        return ImmutableSet.copyOf(this.chunksLoaded.values());
    }

    public synchronized int getLoadedCount() {
        return this.chunksLoaded.size();
    }

    /**
     * Get chunk at specified coordinate if it is already loaded.
     *
     * @param key chunk key
     * @return chunk or null
     */
    @Nullable
    public synchronized Chunk getLoadedChunk(long key) {
        return this.chunksLoaded.get(key);
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
        return this.chunksLoaded.get(chunkKey(x, z));
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
        return this.getChunkFuture(x, z, generate).join();
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
        final long chunkKey = Level.chunkKey(chunkX, chunkZ);
        this.chunkLastAccessTimes.put(chunkKey, System.currentTimeMillis());
        CompletableFuture<Chunk> future = this.chunkFutures.get(chunkKey);
        boolean unloadedChunk = false;
        if (future == null) {
            unloadedChunk = true;
            future = this.provider.readChunk(new ChunkBuilder(chunkX, chunkZ, this.level)).thenApply(chunk -> {
                if (chunk == null) {
                    return new Chunk(chunkX, chunkZ, this.level);
                }
                return chunk;
            });
        }

        if (generate) {
            future = future.thenApplyAsync(this.chunkGenerateFunction);
        }

        if (populate) {
            future = this.populateChunk(future, chunkX, chunkZ, generate);
        }

        if (unloadedChunk) {
            future.whenComplete((chunk, throwable) -> {
                if (throwable != null) {
                    log.warn(new ParameterizedMessage("Unable to load chunk ({}, {}) in level {} ",
                            chunkX, chunkZ, this.level.getId()), throwable);
                    synchronized (LevelChunkManager.this) {
                        this.chunkFutures.remove(chunkKey);
                    }
                    return;
                }

                long currentTime = System.currentTimeMillis();
                synchronized (LevelChunkManager.this) {
                    this.chunkLoadedTimes.put(chunkKey, currentTime);
                    this.chunksLoaded.put(chunkKey, chunk);
                }
            });
        }
        this.chunkFutures.put(chunkKey, future);

        return future;
    }

    public synchronized boolean isChunkLoaded(long hash) {
        return this.chunksLoaded.containsKey(hash);
    }

    public synchronized boolean isChunkLoaded(int x, int z) {
        return this.chunksLoaded.containsKey(chunkKey(x, z));
    }

    public synchronized boolean unloadChunk(long hash) {
        Chunk chunk = this.chunksLoaded.get(hash);
        if (chunk == null) {
            return true;
        }
        return this.unloadChunk(chunk);
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
        return this.unloadChunk(Level.chunkKey(chunk.getX(), chunk.getZ()), save, safe);
    }

    public synchronized boolean unloadChunk(long chunkKey, boolean save, boolean safe) {
        boolean success = unloadChunk0(chunkKey, save, safe);
        if (success) {
            this.chunksLoaded.remove(chunkKey);
        }
        return success;
    }

    private boolean unloadChunk0(long chunkKey, boolean save, boolean safe) {
        Chunk chunk = this.chunksLoaded.get(chunkKey);
        if (chunk == null) {
            return true;
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

            this.chunkFutures.remove(chunkKey);
            this.chunkLastAccessTimes.remove(chunkKey);
            this.chunkLoadedTimes.remove(chunkKey);

            chunk.close();
            return true;
        }
    }

    public void saveChunks() {

    }

    public void saveChunk(Chunk chunk) {
        Preconditions.checkNotNull(chunk, "chunk");
        Preconditions.checkArgument(chunk.getLevel() == this.level,
                "Chunk is not from this ChunkManager's Level");
        if (chunk.isDirty()) {
            this.provider.saveChunk(chunk);
        }
    }

    @Nonnull
    public synchronized CompletableFuture<Chunk> regenerateChunk(int x, int z) {
        long chunkKey = Level.chunkKey(x, z);
        long currentTime = System.currentTimeMillis();
        this.chunkLastAccessTimes.put(chunkKey, currentTime);

        CompletableFuture<Chunk> future = this.chunkFutures.get(chunkKey);

        if (future == null) {
            // No need to load the old chunk
            Chunk newChunk = new Chunk(x, z, this.level);
            this.chunksLoaded.put(chunkKey, newChunk);
            future = CompletableFuture.completedFuture(newChunk);
            this.chunkLoadedTimes.put(chunkKey, currentTime);
        } else {
            future = future.thenApply(chunk -> {
                chunk.clear();
                return chunk;
            });
        }

        future = future.thenApplyAsync(this.chunkGenerateFunction);
        future = this.populateChunk(future, x, z, true);
        this.chunkFutures.put(chunkKey, future);
        return future;
    }

    private CompletableFuture<Chunk> populateChunk(CompletableFuture<Chunk> future, int chunkX, int chunkZ,
                                                   boolean generate) {
        // Load and generate chunks around the chunk to be populated.
        List<CompletableFuture<Chunk>> chunksToLoad = new ArrayList<>(8);
        for (int z = chunkZ - 1, maxZ = chunkZ + 1; z <= maxZ; z++) {
            for (int x = chunkX - 1, maxX = chunkX + 1; x <= maxX; x++) {
                if (x == chunkX && z == chunkZ) continue;
                chunksToLoad.add(this.getChunkFuture(x, z, true, false));
            }
        }
        CompletableFuture<List<Chunk>> aroundFuture = CompletableFutures.allAsList(chunksToLoad);

        // If the chunk is being generated we need to make sure this happens after.
        if (generate) {
            future = future.thenCombine(aroundFuture, this.chunkPopulateFunction);
        } else {
            future = future.thenCombineAsync(aroundFuture, this.chunkPopulateFunction);
        }
        return future;
    }

    public synchronized void tick() {
        long time = System.currentTimeMillis();

        // Spawn chunk
        final int spawnX = this.level.getSafeSpawn().getFloorX() >> 4;
        final int spawnZ = this.level.getSafeSpawn().getFloorZ() >> 4;
        final int spawnRadius = 1;//server.getConfiguration().getAdvanced().getSpawnChunkRadius();

        Config config = this.level.getServer().getConfig();

        // Do chunk garbage collection
        try (Timing ignored = this.level.timings.doChunkGC.startTiming()) {
            ObjectIterator<Long2ObjectMap.Entry<Chunk>> iterator = this.chunksLoaded.long2ObjectEntrySet().iterator();
            while (iterator.hasNext()) {
                Long2ObjectMap.Entry<Chunk> entry = iterator.next();
                long chunkKey = entry.getLongKey();
                Chunk chunk = entry.getValue();

                if (Math.abs(chunk.getX() - spawnX) <= spawnRadius || Math.abs(chunk.getZ() - spawnZ) <= spawnRadius) {
                    continue; // Already loaded
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

                if (!chunk.getLoaders().isEmpty()) {
                    continue;
                }

                if (this.unloadChunk0(chunkKey, true, true)) {
                    iterator.remove();

                    if (log.isTraceEnabled()) {
                        log.trace("Cleared chunk ({},{}) from {}", chunk.getX(), chunk.getZ(), level.getId());
                    }
                }
            }
        }
    }
}
