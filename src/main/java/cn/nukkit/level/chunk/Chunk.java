package cn.nukkit.level.chunk;

import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.BlockUpdate;
import cn.nukkit.level.ChunkLoader;
import cn.nukkit.level.Level;
import cn.nukkit.level.chunk.bitarray.BitArrayVersion;
import cn.nukkit.player.Player;
import cn.nukkit.utils.ChunkException;
import cn.nukkit.utils.Identifier;
import co.aikar.timings.Timing;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.math.vector.Vector4i;
import com.nukkitx.nbt.NbtUtils;
import com.nukkitx.nbt.stream.NBTOutputStream;
import com.nukkitx.network.VarInts;
import com.nukkitx.protocol.bedrock.packet.LevelChunkPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import lombok.NonNull;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.ThreadSafe;
import java.io.Closeable;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.google.common.base.Preconditions.checkNotNull;

@Log4j2
@ThreadSafe
@ParametersAreNonnullByDefault
public final class Chunk implements IChunk, Closeable {

    public static final int SECTION_COUNT = 16;

    static final int ARRAY_SIZE = 256;

    private static final ChunkSection EMPTY = new ChunkSection(new BlockStorage[]{new BlockStorage(BitArrayVersion.V1),
            new BlockStorage(BitArrayVersion.V1)});

    private final Lock readLock; //avoid pointer chasing and an additional interface method call
    private final Lock writeLock;

    private final UnsafeChunk unsafe;

    private final Set<ChunkLoader> loaders = Collections.newSetFromMap(new IdentityHashMap<>());

    private final Set<Player> playerLoaders = Collections.newSetFromMap(new IdentityHashMap<>());

    private SoftReference<LevelChunkPacket> cached = null;

    private Collection<ChunkDataLoader> chunkDataLoaders;

    private List<BlockUpdate> blockUpdates;
    
    private final LockableChunk readLockable;
    private final LockableChunk writeLockable;

    public Chunk(int x, int z, Level level) {
        this(new UnsafeChunk(x, z, level));
    }

    Chunk(UnsafeChunk unsafe, Collection<ChunkDataLoader> chunkDataLoaders, List<BlockUpdate> blockUpdates) {
        this(unsafe);
        this.chunkDataLoaders = checkNotNull(chunkDataLoaders, "chunkEntityLoaders");
        this.blockUpdates = checkNotNull(blockUpdates, "blockUpdates");
    }
    
    private Chunk(@NonNull UnsafeChunk unsafe) {
        this.unsafe = unsafe;
        
        ReadWriteLock lock = new ReentrantReadWriteLock();
        this.readLock = lock.readLock();
        this.writeLock = lock.writeLock();
        
        this.readLockable = new LockableChunk(unsafe, this.readLock);
        this.writeLockable = new LockableChunk(unsafe, this.writeLock);
    }

    public void init() {
        boolean init = this.unsafe.init();
        if (init) {
            try (Timing ignored = unsafe.getLevel().timings.syncChunkLoadEntitiesTimer.startTiming()) {
                boolean dirty = false;

                for (ChunkDataLoader chunkDataLoader : this.chunkDataLoaders) {
                    if (chunkDataLoader.load(this)) {
                        dirty = true;
                    }
                }

                this.setDirty(dirty);
            }

            for (BlockUpdate update : blockUpdates) {
                this.unsafe.getLevel().scheduleUpdate(update);
            }
            this.blockUpdates = null;
        }
    }

    @Nonnull
    @Override
    public ChunkSection getOrCreateSection(int y) {
        this.writeLock.lock();
        try {
            return unsafe.getOrCreateSection(y);
        } finally {
            this.writeLock.unlock();
        }
    }

    @Nullable
    @Override
    public ChunkSection getSection(int y) {
        this.readLock.lock();
        try {
            return unsafe.getSection(y);
        } finally {
            this.readLock.unlock();
        }
    }

    @Nonnull
    @Override
    public ChunkSection[] getSections() {
        this.readLock.lock();
        try {
            ChunkSection[] sections = unsafe.getSections();
            return Arrays.copyOf(sections, sections.length);
        } finally {
            this.readLock.unlock();
        }
    }

    @Nonnull
    @Override
    public Block getBlock(int x, int y, int z, int layer) {
        this.readLock.lock();
        try {
            return unsafe.getBlock(x, y, z, layer);
        } finally {
            this.readLock.unlock();
        }
    }

    @Nonnull
    @Override
    public Identifier getBlockId(int x, int y, int z, int layer) {
        this.readLock.lock();
        try {
            return unsafe.getBlockId(x, y, z, layer);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public int getBlockRuntimeIdUnsafe(int x, int y, int z, int layer) {
        this.readLock.lock();
        try {
            return unsafe.getBlockRuntimeIdUnsafe(x, y, z, layer);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public int getBlockData(int x, int y, int z, int layer) {
        this.readLock.lock();
        try {
            return unsafe.getBlockData(x, y, z, layer);
        } finally {
            this.readLock.unlock();
        }
    }

    @Nonnull
    @Override
    public Block getAndSetBlock(int x, int y, int z, int layer, Block block) {
        this.writeLock.lock();
        try {
            return unsafe.getAndSetBlock(x, y, z, layer, block);
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public void setBlock(int x, int y, int z, int layer, Block block) {
        this.writeLock.lock();
        try {
            unsafe.setBlock(x, y, z, layer, block);
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public void setBlockId(int x, int y, int z, int layer, Identifier id) {
        this.writeLock.lock();
        try {
            unsafe.setBlockId(x, y, z, layer, id);
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public void setBlockRuntimeIdUnsafe(int x, int y, int z, int layer, int runtimeId) {
        this.writeLock.lock();
        try {
            unsafe.setBlockRuntimeIdUnsafe(x, y, z, layer, runtimeId);
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public void setBlockData(int x, int y, int z, int layer, int data) {
        this.writeLock.lock();
        try {
            unsafe.setBlockData(x, y, z, layer, data);
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public int getBiome(int x, int z) {
        this.readLock.lock();
        try {
            return unsafe.getBiome(x, z);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public synchronized void setBiome(int x, int z, int biome) {
        this.writeLock.lock();
        try {
            unsafe.setBiome(x, z, biome);
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public byte getSkyLight(int x, int y, int z) {
        this.readLock.lock();
        try {
            return unsafe.getSkyLight(x, y, z);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public void setSkyLight(int x, int y, int z, int level) {
        this.writeLock.lock();
        try {
            unsafe.setSkyLight(x, y, z, level);
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public byte getBlockLight(int x, int y, int z) {
        this.readLock.lock();
        try {
            return unsafe.getBlockLight(x, y, z);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public void setBlockLight(int x, int y, int z, int level) {
        this.writeLock.lock();
        try {
            unsafe.setBlockLight(x, y, z, level);
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public int getHighestBlock(int x, int z) {
        this.readLock.lock();
        try {
            return this.unsafe.getHighestBlock(x, z);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public void addEntity(@Nonnull Entity entity) {
        this.writeLock.lock();
        try {
            unsafe.addEntity(entity);
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public void removeEntity(Entity entity) {
        this.writeLock.lock();
        try {
            unsafe.removeEntity(entity);
        } finally {
            this.writeLock.unlock();
        }
    }

    public static short blockKey(Vector3i vector) {
        return blockKey(vector.getX(), vector.getY(), vector.getZ());
    }

    public static short blockKey(int x, int y, int z) {
        return (short) ((x & 0xf) | ((z & 0xf) << 4) | ((y & 0xff) << 9));
    }

    public static Vector3i fromKey(long chunkKey, short blockKey) {
        int x = (blockKey & 0xf) | (fromKeyX(chunkKey) << 4);
        int z = ((blockKey >>> 4) & 0xf) | (fromKeyZ(chunkKey) << 4);
        int y = (blockKey >>> 8) & 0xff;
        return Vector3i.from(x, y, z);
    }

    @Override
    public int getX() {
        return unsafe.getX();
    }

    @Override
    public int getZ() {
        return unsafe.getZ();
    }

    @Nonnull
    @Override
    public Level getLevel() {
        return unsafe.getLevel();
    }

    @Nonnull
    @Override
    public byte[] getBiomeArray() {
        this.readLock.lock();
        try {
            return this.unsafe.getBiomeArray().clone();
        } finally {
            this.readLock.unlock();
        }
    }

    @Nonnull
    @Override
    public int[] getHeightMapArray() {
        this.readLock.lock();
        try {
            return this.unsafe.getHeightMapArray().clone();
        } finally {
            this.readLock.unlock();
        }
    }

    @Nonnull
    @Override
    public Set<Player> getPlayers() {
        this.readLock.lock();
        try {
            return new HashSet<>(unsafe.getPlayers());
        } finally {
            this.readLock.unlock();
        }
    }

    @Nonnull
    @Override
    public Set<Entity> getEntities() {
        this.readLock.lock();
        try {
            return new HashSet<>(unsafe.getEntities());
        } finally {
            this.readLock.unlock();
        }
    }

    public static Vector4i fromKey(long chunkKey, int blockKey) {
        int layer = blockKey & 0x1;
        int x = ((blockKey >>> 1) & 0xf) | (fromKeyX(chunkKey) << 4);
        int z = ((blockKey >>> 5) & 0xf) | (fromKeyZ(chunkKey) << 4);
        int y = (blockKey >>> 9) & 0xff;
        return Vector4i.from(x, y, z, layer);
    }

    @Override
    public int getState() {
        return this.unsafe.getState();
    }

    @Override
    public int setState(int next) {
        return this.unsafe.setState(next);
    }

    @Override
    public boolean isDirty() {
        return this.unsafe.isDirty();
    }

    @Override
    public boolean clearDirty() {
        return this.unsafe.clearDirty();
    }

    @Synchronized("loaders")
    public void addLoader(ChunkLoader chunkLoader) {
        Preconditions.checkNotNull(chunkLoader, "chunkLoader");
        this.loaders.add(chunkLoader);
        if (chunkLoader instanceof Player) {
            this.playerLoaders.add((Player) chunkLoader);
        }
    }

    @Synchronized("loaders")
    public void removeLoader(ChunkLoader chunkLoader) {
        Preconditions.checkNotNull(chunkLoader, "chunkLoader");
        this.loaders.remove(chunkLoader);
        if (chunkLoader instanceof Player) {
            this.playerLoaders.remove(chunkLoader);
        }
    }

    @Nonnull
    @Synchronized("loaders")
    public Set<ChunkLoader> getLoaders() {
        return ImmutableSet.copyOf(loaders);
    }

    @Nonnull
    @Synchronized("loaders")
    public Set<Player> getPlayerLoaders() {
        return new HashSet<>(playerLoaders);
    }


    private synchronized void clearCache() {
        // Clear cached packet
        if (this.cached != null) {
            LevelChunkPacket packet = this.cached.get();
            this.cached = null;
        }
    }

    public void tick(int tick) {
        //todo
    }

    public LockableChunk readLockable() {
        return this.readLockable;
    }

    public LockableChunk writeLockable() {
        return this.writeLockable;
    }

    public void clear() {
        this.writeLock.lock();
        try {
            unsafe.clear();
            this.blockUpdates.clear();
            this.chunkDataLoaders = null;
        } finally {
            this.writeLock.unlock();
        }
        this.clearCache();
    }

    @Override
    public synchronized void close() {
        this.writeLock.lock();
        try {
            unsafe.close();
        } finally {
            this.writeLock.unlock();
        }
        this.clearCache();
    }

//    private static class CacheSoftReference extends FinalizableSoftReference<LevelChunkPacket> {
//        private final LevelChunkPacket hardRef;
//
//        /**
//         * Constructs a new finalizable soft reference.
//         *
//         * @param referent to softly reference
//         * @param queue    that should finalize the referent
//         */
//        private CacheSoftReference(LevelChunkPacket referent, FinalizableReferenceQueue queue) {
//            super(referent, queue);
//            this.hardRef = referent;
//        }
//
//        @Override
//        public void finalizeReferent() {
//            this.hardRef.release();
//        }
//    }

    @Override
    public void addBlockEntity(BlockEntity blockEntity) {
        this.writeLock.lock();
        try {
            unsafe.addBlockEntity(blockEntity);
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public void removeBlockEntity(BlockEntity blockEntity) {
        this.writeLock.lock();
        try {
            unsafe.removeBlockEntity(blockEntity);
        } finally {
            this.writeLock.unlock();
        }
    }

    public static int blockKey(int x, int y, int z, int layer) {
        return (layer & 0x1) | ((x & 0xf) << 1) | ((z & 0xf) << 5) | ((y & 0xff) << 9);
    }

    @Override
    public BlockEntity getBlockEntity(int x, int y, int z) {
        this.readLock.lock();
        try {
            return unsafe.getBlockEntity(x, y, z);
        } finally {
            this.readLock.unlock();
        }
    }

    @Nonnull
    @Override
    public Set<BlockEntity> getBlockEntities() {
        this.readLock.lock();
        try {
            return new HashSet<>(unsafe.getBlockEntities());
        } finally {
            this.readLock.unlock();
        }
    }

    public static long key(int x, int z) {
        return (((long) x) << 32) | (z & 0xffffffffL);
    }

    public static int fromKeyX(long key) {
        return (int) (key >> 32);
    }

    public static int fromKeyZ(long key) {
        return (int) key;
    }

    @Override
    public void setDirty(boolean dirty) {
        unsafe.setDirty(dirty);
    }

    @Nonnull
    public synchronized LevelChunkPacket createChunkPacket() {
        if (UnsafeChunk.CLEAR_CACHE_FIELD.compareAndSet(unsafe, 1, 0)) {
            this.clearCache();
        }
        if (this.cached != null) {
            LevelChunkPacket packet = this.cached.get();
            if (packet != null) {
                return packet;
            } else {
                this.cached = null;
            }
        }

        LevelChunkPacket packet = new LevelChunkPacket();
        packet.setChunkX(this.getX());
        packet.setChunkZ(this.getZ());

        this.readLock.lock();
        try {
            ChunkSection[] sections = unsafe.getSections();

            int subChunkCount = SECTION_COUNT - 1; // index
            while (subChunkCount >= 0 && (sections[subChunkCount] == null || sections[subChunkCount].isEmpty())) {
                subChunkCount--;
            }
            subChunkCount++; // length

            ChunkSection[] networkSections = Arrays.copyOf(sections, subChunkCount);
            for (int i = 0; i < subChunkCount; i++) {
                if (networkSections[i] == null) {
                    networkSections[i] = EMPTY;
                }
            }

            packet.setSubChunksLength(subChunkCount);

            ByteBuf buffer = Unpooled.buffer();
            try {
                for (int i = 0; i < subChunkCount; i++) {
                    networkSections[i].writeToNetwork(buffer);
                }

                buffer.writeBytes(unsafe.getBiomeArray()); // Biomes - 256 bytes
                buffer.writeByte(0); // Border blocks size - Education Edition only

                // Extra Data length. Replaced by second block layer.
                VarInts.writeUnsignedInt(buffer, 0);

                Collection<BlockEntity> tiles = unsafe.getBlockEntities();
                // Block entities
                if (!tiles.isEmpty()) {
                    try (ByteBufOutputStream stream = new ByteBufOutputStream(buffer);
                         NBTOutputStream nbtOutputStream = NbtUtils.createNetworkWriter(stream)) {
                        tiles.forEach(blockEntity -> {
                            if (blockEntity.isSpawnable()) {
                                try {
                                    nbtOutputStream.write(blockEntity.getChunkTag());
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });
                    }
                }

                byte[] data = new byte[buffer.readableBytes()];
                buffer.readBytes(data);

                packet.setData(data);

                this.cached = new SoftReference<>(packet);

                return packet;
            } catch (IOException e) {
                log.error("Error whilst encoding chunk", e);
                this.cached = null;
                throw new ChunkException("Unable to create chunk packet", e);
            } finally {
                buffer.release();
            }
        } finally {
            this.readLock.unlock();
        }
    }
}
