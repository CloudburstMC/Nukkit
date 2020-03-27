package cn.nukkit.player.manager;

import cn.nukkit.entity.Entity;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.player.Player;
import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.packet.ChunkRadiusUpdatedPacket;
import com.nukkitx.protocol.bedrock.packet.LevelChunkPacket;
import com.nukkitx.protocol.bedrock.packet.NetworkChunkPublisherUpdatePacket;
import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongConsumer;

import static com.google.common.base.Preconditions.checkArgument;

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
                for (Entity entity : chunk.getEntities()) {
                    entity.despawnFrom(this.player);
                }
            }
        };
    }

    public synchronized void sendQueued() {
        int chunksPerTick = 16; //this.player.getServer().getConfig("chunk-sending.per-tick", 4);
        ObjectIterator<Long2ObjectMap.Entry<LevelChunkPacket>> sendQueueIterator = this.sendQueue.long2ObjectEntrySet().iterator();
        // Remove chunks which are out of range
        while (sendQueueIterator.hasNext()) {
            Long2ObjectMap.Entry<LevelChunkPacket> entry = sendQueueIterator.next();
            long key = entry.getLongKey();
            if (!this.loadedChunks.contains(key)) {
//                LevelChunkPacket packet = entry.getValue();
//                if (packet != null) {
//                    packet.release();
//                }
                sendQueueIterator.remove();

                Chunk chunk = this.player.getLevel().getLoadedChunk(key);
                if (chunk != null) {
                    chunk.removeLoader(this.player);
                }
            }
        }

        LongList list = new LongArrayList(this.sendQueue.keySet());

        try (Timing ignored = Timings.playerChunkOrderTimer.startTiming()) {
            // Order chunks around player.
            list.unstableSort(this.comparator);
        }

        try (Timing ignored = Timings.playerChunkSendTimer.startTiming()) {
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
                this.player.sendPacket(packet);

                Chunk chunk = this.player.getLevel().getLoadedChunk(key);
                checkArgument(chunk != null, "Attempted to send unloaded chunk (%s, %s) to %s",
                        Chunk.fromKeyX(key), Chunk.fromKeyZ(key), this.player.getName());

                // Spawn entities
                for (Entity entity : chunk.getEntities()) {
                    if (entity != this.player && !entity.isClosed() && entity.isAlive()) {
                        entity.spawnTo(this.player);
                    }
                }

                chunksPerTick--;
                this.chunksSentCounter.incrementAndGet();
            }
        }
    }

    public void queueNewChunks() {
        this.queueNewChunks(this.player.getPosition());
    }

    public void queueNewChunks(Vector3f pos) {
        this.queueNewChunks(pos.getFloorX() >> 4, pos.getFloorZ() >> 4);
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

                final long key = Chunk.key(cx, cz);

                chunksForRadius.add(key);
                if (this.loadedChunks.add(key)) {
                    chunksToLoad.add(key);
                }
            }
        }

        boolean loadedChunksChanged = this.loadedChunks.retainAll(chunksForRadius);
        if (loadedChunksChanged || !chunksToLoad.isEmpty()) {
            NetworkChunkPublisherUpdatePacket packet = new NetworkChunkPublisherUpdatePacket();
            packet.setPosition(this.player.getPosition().toInt());
            packet.setRadius(this.radius);
            this.player.sendPacket(packet);
        }

        // Order chunks for smoother loading
        chunksToLoad.sort(this.comparator);

        for (final long key : chunksToLoad.toLongArray()) {
            final int cx = Chunk.fromKeyX(key);
            final int cz = Chunk.fromKeyZ(key);

            if (this.sendQueue.putIfAbsent(key, null) == null) {
                this.player.getLevel().getChunkFuture(cx, cz).thenApply(chunk -> {
                    chunk.addLoader(this.player);
                    return chunk;
                }).thenApplyAsync(Chunk::createChunkPacket, this.player.getServer().getScheduler().getAsyncPool())
                        .whenComplete((packet, throwable) -> {
                            synchronized (PlayerChunkManager.this) {
                                if (throwable != null) {
                                    if (this.sendQueue.remove(key, null)) {
                                        this.loadedChunks.remove(key);
                                    }
                                    log.error("Unable to create chunk packet for " + this.player.getName(), throwable);
                                } else if (!this.sendQueue.replace(key, null, packet)) {
                                    // The chunk was already loaded!?
                                    if (this.sendQueue.containsKey(key)) {
                                        log.warn("Chunk ({},{}) already loaded for {}, value {}", cx, cz,
                                                this.player.getName(), this.sendQueue.get(key));
                                    }
//                                    packet.release();
                                }
                            }
                        });
            }
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
            chunkRadiusUpdatePacket.setRadius(radius >> 4);
            this.player.sendPacket(chunkRadiusUpdatePacket);
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
        return this.isChunkInView(Chunk.key(x, z));
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

    public synchronized void resendChunk(int chunkX, int chunkZ) {
        long chunkKey = Chunk.key(chunkX, chunkZ);
        this.loadedChunks.remove(chunkKey);
        removeChunkLoader.accept(chunkKey);
    }

    public void prepareRegion(Vector3f pos) {
        this.prepareRegion(pos.getFloorX() >> 4, pos.getFloorZ() >> 4);
    }

    public void prepareRegion(int chunkX, int chunkZ) {
        this.clear();
        this.queueNewChunks(chunkX, chunkZ);
    }

    public synchronized void clear() {
        // Release all chunks that are loading.
//        this.sendQueue.values().forEach(levelChunkPacket -> {
//            if (levelChunkPacket != null) {
//                levelChunkPacket.release();
//            }
//        });
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
            int x1 = Chunk.fromKeyX(o1);
            int z1 = Chunk.fromKeyZ(o1);
            int x2 = Chunk.fromKeyX(o2);
            int z2 = Chunk.fromKeyZ(o2);
            int spawnX = this.player.getPosition().getFloorX() >> 4;
            int spawnZ = this.player.getPosition().getFloorZ() >> 4;

            return Integer.compare(distance(spawnX, spawnZ, x1, z1), distance(spawnX, spawnZ, x2, z2));
        }
    }
}
