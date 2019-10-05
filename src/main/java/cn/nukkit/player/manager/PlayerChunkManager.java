package cn.nukkit.player.manager;

import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.network.protocol.ChunkRadiusUpdatedPacket;
import cn.nukkit.network.protocol.LevelChunkPacket;
import cn.nukkit.network.protocol.NetworkChunkPublisherUpdatePacket;
import cn.nukkit.player.Player;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongConsumer;

@Log4j2
public class PlayerChunkManager {

    private final Player player;
    private final AroundPlayerChunkComparator comparator;
    private final LongSet loadedChunks = new LongOpenHashSet();
    private final Long2ObjectMap<LevelChunkPacket> sendQueue = new Long2ObjectOpenHashMap<>();
    private final AtomicLong chunksSentCounter = new AtomicLong();
    private final LongConsumer removeChunkLoader;
    private volatile int radius;

    public PlayerChunkManager(Player player) {
        this.player = player;
        this.comparator = new AroundPlayerChunkComparator(player);
        this.removeChunkLoader = chunkKey -> {
            Chunk chunk = this.player.getLevel().getLoadedChunk(chunkKey);
            if (chunk != null) {
                chunk.removeLoader(this.player);
            }
        };
    }

    public synchronized void sendQueued() {
        int chunksPerTick = 16; //this.player.getServer().getConfig("chunk-sending.per-tick", 4);
        ObjectIterator<Long2ObjectMap.Entry<LevelChunkPacket>> sendQueueIterator = this.sendQueue.long2ObjectEntrySet().iterator();
        // Remove chunks which are out of range
        while (sendQueueIterator.hasNext()) {
            Long2ObjectMap.Entry<LevelChunkPacket> entry = sendQueueIterator.next();
            if (!this.loadedChunks.contains(entry.getLongKey())) {
                LevelChunkPacket packet = entry.getValue();
                if (packet != null) {
                    packet.release();
                }
                sendQueueIterator.remove();
            }
        }

        LongList list = new LongArrayList(this.sendQueue.keySet());

        // Order chunks around player.
        list.sort(this.comparator);

        for (long key : list.toLongArray()) {
            if (chunksPerTick < 0) {
                break;
            }

            LevelChunkPacket packet = this.sendQueue.get(key);
            if (packet == null) {
                // Next packet is not available.
                break;
            }

            this.sendQueue.remove(key);
            this.player.dataPacket(packet);

            Chunk chunk = this.player.getLevel().getLoadedChunk(key);
            Preconditions.checkArgument(chunk != null, "Unloaded chunk sent to %s",
                    this.player.getName());

            chunk.addLoader(this.player);
            // Spawn entities
            for (Entity entity : chunk.getEntities()) {
                if (this.player != entity && !entity.closed && entity.isAlive()) {
                    entity.spawnTo(this.player);
                }
            }

            chunksPerTick--;
            this.chunksSentCounter.incrementAndGet();
        }
    }

    public void queueNewChunks() {
        this.queueNewChunks(this.player.getChunkX(), this.player.getChunkZ());
    }

    public synchronized void queueNewChunks(int chunkX, int chunkZ) {
        int radius = this.getChunkRadius();
        int radiusSqr = radius * radius;

        LongSet chunksForRadius = new LongOpenHashSet();

        LongSet sentCopy = new LongOpenHashSet(this.loadedChunks);

        LongList chunksToLoad = new LongArrayList();

        for (int x = -radius; x <= radius; ++x) {
            for (int z = -radius; z <= radius; ++z) {
                // Chunk radius is circular so we need to remove the corners.
                if ((x * x) + (z * z) > radiusSqr) {
                    continue;
                }

                int cx = chunkX + x;
                int cz = chunkZ + z;

                final long key = Level.chunkKey(cx, cz);

                chunksForRadius.add(key);
                if (this.loadedChunks.add(key)) {
                    chunksToLoad.add(key);
                }
            }
        }

        boolean loadedChunksChanged = this.loadedChunks.retainAll(chunksForRadius);
        if (loadedChunksChanged || !chunksToLoad.isEmpty()) {
            NetworkChunkPublisherUpdatePacket packet = new NetworkChunkPublisherUpdatePacket();
            packet.position = this.player.asBlockVector3();
            packet.radius = this.radius;
            this.player.dataPacket(packet);
        }

        // Order chunks for faster loading
        chunksToLoad.sort(this.comparator);

        for (final long key : chunksToLoad.toLongArray()) {
            int cx = Level.getHashX(key);
            int cz = Level.getHashZ(key);

            this.sendQueue.put(key, null);

            this.player.getLevel().getChunkFuture(cx, cz).thenApplyAsync(Chunk::createChunkPacket)
                    .whenCompleteAsync((packet, throwable) -> {
                        synchronized (PlayerChunkManager.this) {
                            if (throwable != null) {
                                if (this.sendQueue.remove(key, null)) {
                                    this.loadedChunks.remove(key);
                                }
                                log.error("Unable to create chunk packet for " + this.player.getName(), throwable);
                            } else if (!this.sendQueue.replace(key, null, packet)) {
                                // The chunk was already loaded!?
                                log.warn("Chunk already loaded for {}", this.player.getName());
                                packet.release();
                            }
                        }
                    });
        }

        sentCopy.removeAll(chunksForRadius);
        // Remove player from chunk loaders
        sentCopy.forEach(this.removeChunkLoader);
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        if (this.radius != radius) {
            this.radius = radius;
            ChunkRadiusUpdatedPacket chunkRadiusUpdatePacket = new ChunkRadiusUpdatedPacket();
            chunkRadiusUpdatePacket.radius = radius >> 4;
            this.player.dataPacket(chunkRadiusUpdatePacket);
            this.queueNewChunks();
        }
    }

    public int getChunkRadius() {
        return this.radius >> 4;
    }

    public void setChunkRadius(int chunkRadius) {
        chunkRadius = NukkitMath.clamp(chunkRadius, 8,
                this.player.getServer().getConfig("chunk-sending.max-chunk-radius", 10));
        this.setRadius(chunkRadius << 4);
    }

    public boolean isChunkInView(int x, int z) {
        return this.isChunkInView(Level.chunkKey(x, z));
    }

    public synchronized boolean isChunkInView(long key) {
        return this.loadedChunks.contains(key);
    }

    public long getChunksSent() {
        return chunksSentCounter.get();
    }

    public LongSet getLoadedChunks() {
        return LongSets.unmodifiable(this.loadedChunks);
    }

    public void prepareRegion(int chunkX, int chunkZ) {
        this.clear();
        this.queueNewChunks(chunkX, chunkZ);
    }

    public synchronized void clear() {
        // Release all chunks that are loading.
        this.sendQueue.values().forEach(levelChunkPacket -> {
            if (levelChunkPacket != null) {
                levelChunkPacket.release();
            }
        });
        this.sendQueue.clear();

        this.loadedChunks.forEach(this.removeChunkLoader);
        this.loadedChunks.clear();
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static class AroundPlayerChunkComparator implements LongComparator {
        private final Player player;

        public static int distance(int centerX, int centerZ, int x, int z) {
            int dx = centerX - x;
            int dz = centerZ - z;
            return dx * dx + dz * dz;
        }

        @Override
        public int compare(long o1, long o2) {
            int x1 = Level.getHashX(o1);
            int z1 = Level.getHashZ(o1);
            int x2 = Level.getHashX(o2);
            int z2 = Level.getHashZ(o2);
            int spawnX = this.player.getChunkX();
            int spawnZ = this.player.getChunkZ();

            return Integer.compare(distance(spawnX, spawnZ, x1, z1), distance(spawnX, spawnZ, x2, z2));
        }
    }
}
