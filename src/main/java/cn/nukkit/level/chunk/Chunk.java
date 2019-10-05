package cn.nukkit.level.chunk;

import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.BlockUpdate;
import cn.nukkit.level.ChunkLoader;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.chunk.bitarray.BitArrayVersion;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.network.protocol.LevelChunkPacket;
import cn.nukkit.player.Player;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.ChunkException;
import cn.nukkit.utils.Utils;
import co.aikar.timings.Timing;
import com.google.common.base.FinalizableReferenceQueue;
import com.google.common.base.FinalizableSoftReference;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import gnu.trove.TCollections;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TIntShortMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TIntShortHashMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.ThreadSafe;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.*;

@Log4j2
@ThreadSafe
@ParametersAreNonnullByDefault
public final class Chunk implements Closeable {

    public static final int SECTION_COUNT = 16;

    private static final int ARRAY_SIZE = 256;

    private static final int BLOCK_ID_MASK = ~0b11;

    private static final ChunkSection EMPTY = new ChunkSection(new BlockStorage[]{new BlockStorage(BitArrayVersion.V1),
            new BlockStorage(BitArrayVersion.V1)});

    private final int x;

    private final int z;

    private final Level level;

    private final ChunkSection[] sections;

    private final Set<Player> players = new HashSet<>();

    private final Set<Entity> entities = new HashSet<>();

    private final Set<ChunkLoader> loaders = new HashSet<>();

    private final Set<Player> playerLoaders = new HashSet<>();

    private final TIntObjectMap<BlockEntity> tiles = new TIntObjectHashMap<>();

    private final byte[] biomes;

    private final int[] heightMap;

    private final TIntShortMap extraData = new TIntShortHashMap();

    private volatile boolean dirty;

    private volatile boolean initialized;

    private volatile boolean generated;

    private volatile boolean populated;

    private CacheSoftReference cached;

    private Collection<ChunkDataLoader> chunkDataLoaders;

    private List<BlockUpdate> blockUpdates;

    public Chunk(int x, int z, Level level) {
        this.x = x;
        this.z = z;
        this.level = level;
        this.sections = new ChunkSection[SECTION_COUNT];
        this.biomes = new byte[ARRAY_SIZE];
        this.heightMap = new int[ARRAY_SIZE];
    }

    Chunk(int x, int z, Level level, ChunkSection[] sections, Collection<ChunkDataLoader> chunkDataLoaders,
          TIntShortMap extraData, byte[] biomes, int[] heightMap, List<BlockUpdate> blockUpdates) {
        this.x = x;
        this.z = z;
        this.level = level;
        Preconditions.checkNotNull(sections, "sections");
        this.sections = Arrays.copyOf(sections, SECTION_COUNT);
        this.chunkDataLoaders = Preconditions.checkNotNull(chunkDataLoaders, "chunkEntityLoaders");
        Preconditions.checkNotNull(extraData, "extraData");
        this.extraData.putAll(extraData);
        Preconditions.checkNotNull(biomes, "biomes");
        this.biomes = Arrays.copyOf(biomes, ARRAY_SIZE);
        Preconditions.checkNotNull(heightMap, "heightMap");
        this.heightMap = Arrays.copyOf(heightMap, ARRAY_SIZE);
        this.blockUpdates = Preconditions.checkNotNull(blockUpdates, "blockUpdates");
    }

    private static void checkBounds(int x, int y, int z) {
        Preconditions.checkArgument(x >= 0 && x < 16, "x value was %s. Expected 0-15", x);
        Preconditions.checkArgument(y >= 0 && y < 256, "y value was %s. Expected 0-255", y);
        Preconditions.checkArgument(z >= 0 && z < 16, "z value was %s. Expected 0-15", z);
    }

    private static int getXZIndex(int x, int z) {
        int index = z << 4 | x;
        Preconditions.checkElementIndex(index, Chunk.ARRAY_SIZE);
        return index;
    }

    public synchronized void init() {
        if (!this.initialized) {
            try (Timing ignored = this.level.timings.syncChunkLoadEntitiesTimer.startTiming()) {
                boolean dirty = false;

                for (ChunkDataLoader chunkDataLoader : this.chunkDataLoaders) {
                    if (chunkDataLoader.load(this)) {
                        dirty = true;
                    }
                }

                if (dirty) {
                    this.setDirty();
                }

            }
            for (BlockUpdate blockUpdate : this.blockUpdates) {
                this.level.scheduleUpdate(blockUpdate);
            }
            this.blockUpdates = null;

            this.initialized = true;
        }
    }

    @Nonnull
    public synchronized ChunkSection getOrCreateSection(int y) {
        Preconditions.checkElementIndex(y, sections.length);

        ChunkSection section = this.sections[y];
        if (section == null) {
            section = new ChunkSection();
            this.sections[y] = section;
        }
        return section;
    }

    @Nullable
    public synchronized ChunkSection getSection(int y) {
        Preconditions.checkElementIndex(y, sections.length);
        return this.sections[y];
    }

    @Nonnull
    public ChunkSection[] getSections() {
        return Arrays.copyOf(this.sections, SECTION_COUNT);
    }

    @Nonnull
    public Block getBlock(int x, int y, int z) {
        return this.getBlock(x, y, z, 0);
    }

    @Nonnull
    public Block getBlock(int x, int y, int z, int layer) {
        int fullBlock = this.getFullBlock(x, y, z, layer);
        int xPos = this.x << 4 | x & 0xf;
        int zPos = this.z << 4 | z & 0xf;
        return Block.get(fullBlock >>> 4, fullBlock & 0xf, new Position(xPos, y, zPos, this.level));
    }

    public int getBlockId(int x, int y, int z) {
        return this.getBlockId(x, y, z, 0);
    }

    public int getBlockId(int x, int y, int z, int layer) {
        return this.getFullBlock(x, y, z, layer) >>> 4;
    }

    public int getBlockData(int x, int y, int z) {
        return this.getBlockData(x, y, z, 0);
    }

    public int getBlockData(int x, int y, int z, int layer) {
        return this.getFullBlock(x, y, z, layer) & 0xf;
    }

    public int getFullBlock(int x, int y, int z) {
        return this.getFullBlock(x, y, z, 0);
    }

    public int getFullBlock(int x, int y, int z, int layer) {
        Chunk.checkBounds(x, y, z);
        ChunkSection section = this.getSection(y >> 4);
        if (section == null) {
            return 0;
        }
        return section.getFullBlock(x, y & 0xf, z, layer);
    }

    @Nonnull
    public Block getAndSetBlock(int x, int y, int z, Block block) {
        return this.getAndSetBlock(x, y, z, 0, block);
    }

    @Nonnull
    public Block getAndSetBlock(int x, int y, int z, int layer, Block block) {
        Block previousBlock = this.getBlock(x, y, z, layer);
        this.setBlock(x, y, z, layer, block);
        return previousBlock;
    }

    public void setBlock(int x, int y, int z, Block block) {
        this.setBlock(x, y, z, 0, block);
    }

    public void setBlock(int x, int y, int z, int layer, Block block) {
        this.setFullBlock(x, y, z, layer, block.getFullId());
    }

    public void setBlockId(int x, int y, int z, int id) {
        this.setBlockId(x, y, z, 0, id);
    }

    public void setBlockId(int x, int y, int z, int layer, int id) {
        this.setFullBlock(x, y, z, layer, id << 4);
    }

    public void setBlockData(int x, int y, int z, int data) {
        this.setBlockData(x, y, z, 0, data);
    }

    public void setBlockData(int x, int y, int z, int layer, int data) {
        int fullBlock = this.getFullBlock(x, y, z, layer);

        this.setFullBlock(x, y, z, layer, (fullBlock & BLOCK_ID_MASK) | data);
    }

    public void setFullBlock(int x, int y, int z, int fullBlock) {
        this.setFullBlock(x, y, z, 0, fullBlock);
    }

    public void setFullBlock(int x, int y, int z, int layer, int fullBlock) {
        Chunk.checkBounds(x, y, z);
        ChunkSection section = this.getSection(y >> 4);
        if (section == null) {
            if (fullBlock == 0) {
                // Setting air in an empty section.
                return;
            }
            section = this.getOrCreateSection(y >> 4);
        }

        section.setFullBlock(x, y & 0xf, z, layer, fullBlock);
    }

    public short getBlockExtraData(int x, int y, int z) {
        return this.getBlockExtraData(Level.chunkBlockKey(x, y, z));
    }

    public synchronized short getBlockExtraData(int index) {
        return this.extraData.get(index);
    }

    public void setBlockExtraData(int x, int y, int z, short data) {
        Chunk.checkBounds(x, y, z);
        this.setBlockExtraData(Level.chunkBlockKey(x, y, z), data);
    }

    public synchronized void setBlockExtraData(int index, short data) {
        boolean dirty = false;
        if (data == 0) {
            if (this.extraData.remove(index) != -1) {
                dirty = true;
            }
        } else {
            this.extraData.put(index, data);
            dirty = true;
        }
        if (dirty) {
            this.setDirty();
        }
    }

    public synchronized int getBiome(int x, int z) {
        return this.biomes[Chunk.getXZIndex(x, z)] & 0xFF;
    }

    public synchronized void setBiome(int x, int z, int biome) {
        int index = Chunk.getXZIndex(x, z);
        int oldBiome = this.biomes[index] & 0xf;
        if (oldBiome != biome) {
            this.biomes[index] = (byte) biome;
            this.setDirty();
        }
    }

    public byte getSkyLight(int x, int y, int z) {
        checkBounds(x, y, z);
        ChunkSection section = this.getSection(y >> 4);
        return section == null ? 0 : section.getSkyLight(x, y, z);
    }

    public void setSkyLight(int x, int y, int z, int level) {
        checkBounds(x, y, z);
        this.getOrCreateSection(y >> 4).setSkyLight(x, y & 0xf, z, (byte) level);
    }

    public byte getBlockLight(int x, int y, int z) {
        checkBounds(x, y, z);
        ChunkSection section = this.getSection(y >> 4);
        return section == null ? 0 : section.getBlockLight(x, y & 0xf, z);
    }

    public void setBlockLight(int x, int y, int z, int level) {
        checkBounds(x, y, z);
        this.getOrCreateSection(y >> 4).setBlockLight(x, y & 0xf, z, (byte) level);
    }

    public void recalculateHeightMap() {
        for (int z = 0; z < 16; ++z) {
            for (int x = 0; x < 16; ++x) {
                this.setHeightMap(x, z, this.getHighestBlock(x, z, false));
            }
        }
    }

    public synchronized int getHeightMap(int x, int z) {
        return this.heightMap[Chunk.getXZIndex(x, z)] & 0xFF;
    }

    public synchronized void setHeightMap(int x, int z, int value) {
        this.heightMap[Chunk.getXZIndex(x, z)] = (byte) value;
    }

    public int getHighestBlock(int x, int z) {
        return this.getHighestBlock(x, z, true);
    }

    public int getHighestBlock(int x, int z, boolean cache) {
        if (cache) {
            int h = this.getHeightMap(x, z);
            if (h != 0 && h != 255) {
                return h;
            }
        }
        for (int y = 255; y >= 0; --y) {
            if (getBlockId(x, y, z) != 0x00) {
                this.setHeightMap(x, z, y);
                return y;
            }
        }
        return 0;
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
            this.playerLoaders.remove((Player) chunkLoader);
        }
    }

    public synchronized void addEntity(Entity entity) {
        Preconditions.checkNotNull(entity, "entity");
        if (entity instanceof Player) {
            this.players.add((Player) entity);
        } else if (this.entities.add(entity) && this.initialized) {
            this.setDirty();
        }
    }

    public synchronized void removeEntity(Entity entity) {
        Preconditions.checkNotNull(entity, "entity");
        if (entity instanceof Player) {
            this.players.remove(entity);
        } else if (this.entities.remove(entity) && this.initialized) {
            this.setDirty();
        }
    }

    public synchronized void addBlockEntity(BlockEntity blockEntity) {
        Preconditions.checkNotNull(blockEntity, "blockEntity");
        int hash = Level.chunkBlockKey(blockEntity.getFloorX() & 0xf, blockEntity.getFloorY(), blockEntity.getFloorZ() & 0xf);
        if (this.tiles.put(hash, blockEntity) != blockEntity && this.initialized) {
            this.setDirty();
        }
    }

    public synchronized void removeBlockEntity(BlockEntity blockEntity) {
        Preconditions.checkNotNull(blockEntity, "blockEntity");
        int hash = Level.chunkBlockKey(blockEntity.getFloorX() & 0xf, blockEntity.getFloorY(), blockEntity.getFloorZ() & 0xf);
        if (this.tiles.remove(hash) == blockEntity && this.initialized) {
            this.setDirty();
        }
    }

    @Nullable
    public synchronized BlockEntity getTile(int x, int y, int z) {
        Chunk.checkBounds(x, y, z);
        return this.tiles.get(Level.chunkBlockKey(x, y, z));
    }

    public void populateSkyLight() {
        for (int z = 0; z < 16; ++z) {
            for (int x = 0; x < 16; ++x) {
                int top = this.getHeightMap(x, z);
                for (int y = 255; y > top; --y) {
                    this.setSkyLight(x, y, z, (byte) 15);
                }
                for (int y = top; y >= 0; --y) {
                    if (Block.solid[this.getBlockId(x, y, z)]) {
                        break;
                    }
                    this.setSkyLight(x, y, z, (byte) 15);
                }
                this.setHeightMap(x, z, this.getHighestBlock(x, z, false));
            }
        }
    }

    @Nonnull
    public synchronized LevelChunkPacket createChunkPacket() {
        if (this.cached != null) {
            LevelChunkPacket packet = this.cached.get();
            if (packet != null) {
                return packet.copy();
            } else {
                this.cached = null;
            }
        }

        LevelChunkPacket packet = new LevelChunkPacket();
        packet.chunkX = this.x;
        packet.chunkZ = this.z;

        int subChunkCount = SECTION_COUNT - 1; // index
        while (subChunkCount >= 0 && (this.sections[subChunkCount] == null || this.sections[subChunkCount].isEmpty())) {
            subChunkCount--;
        }
        subChunkCount++; // length

        ChunkSection[] sections = Arrays.copyOf(this.sections, subChunkCount);
        for (int i = 0; i < subChunkCount; i++) {
            if (sections[i] == null) {
                sections[i] = EMPTY;
            }
        }

        packet.subChunkCount = subChunkCount;

        ByteBuf buffer = Unpooled.buffer();
        try {
            for (int i = 0; i < subChunkCount; i++) {
                sections[i].writeToNetwork(buffer);
            }

            buffer.writeBytes(this.biomes); // Biomes - 256 bytes
            buffer.writeByte(0); // Border blocks size - Education Edition only

            // Extra Data
            TIntShortMap extraData = this.extraData;
            Binary.writeUnsignedVarInt(buffer, extraData.size());
            if (!extraData.isEmpty()) {
                extraData.forEachEntry((position, data) -> {
                    Binary.writeVarInt(buffer, position);
                    buffer.writeShortLE(data);
                    return false;
                });
            }

            // Block entities
            if (!this.tiles.isEmpty()) {
                try (ByteBufOutputStream stream = new ByteBufOutputStream(buffer)) {
                    this.tiles.forEachValue(blockEntity -> {
                        if (blockEntity instanceof BlockEntitySpawnable) {
                            try {
                                NBTIO.write(((BlockEntitySpawnable) blockEntity).getSpawnCompound(), stream,
                                        ByteOrder.LITTLE_ENDIAN, true);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        return false;
                    });
                }
            }

            packet.data = buffer;//.asReadOnly(); // Stop any accidental corruption

            this.cached = new CacheSoftReference(packet, Utils.REFERENCE_QUEUE);

            return packet.copy();
        } catch (IOException e) {
            buffer.release();
            log.error("Error whilst encoding chunk", e);
            this.cached = null;
            throw new ChunkException("Unable to create chunk packet", e);
        }
    }

    /**
     * Get the chunk's X coordinate in the level it was loaded.
     *
     * @return chunk x
     */
    public int getX() {
        return x;
    }

    /**
     * Get the chunk's Z coordinate in the level it was loaded.
     *
     * @return chunk z
     */
    public int getZ() {
        return z;
    }

    /**
     * Get the level the chunk was loaded in.
     *
     * @return chunk level
     */
    @Nonnull
    public Level getLevel() {
        return level;
    }

    /**
     * Get the copy of the biome array.
     *
     * @return biome array
     */
    @Nonnull
    public synchronized byte[] getBiomeArray() {
        return Arrays.copyOf(this.biomes, ARRAY_SIZE);
    }

    /**
     * Get a copy of the height map array.
     *
     * @return height map
     */
    @Nonnull
    public int[] getHeightMapArray() {
        return Arrays.copyOf(this.heightMap, ARRAY_SIZE);
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

    /**
     * Gets an immutable copy of players currently in this chunk
     *
     * @return player set
     */
    @Nonnull
    public synchronized Set<Player> getPlayers() {
        return ImmutableSet.copyOf(players);
    }

    /**
     * Gets an immutable copy of entities currently in this chunk
     *
     * @return entity set
     */
    @Nonnull
    public synchronized Set<Entity> getEntities() {
        return ImmutableSet.copyOf(this.entities);
    }

    /**
     * Gets an immutable copy of all block entities within the current chunk.
     *
     * @return block entity collection
     */
    @Nonnull
    public synchronized Collection<BlockEntity> getBlockEntities() {
        return ImmutableList.copyOf(this.tiles.valueCollection());
    }

    public synchronized TIntShortMap getBlockExtraData() {
        return TCollections.unmodifiableMap(this.extraData);
    }

    public boolean isGenerated() {
        return generated;
    }

    public void setGenerated() {
        this.generated = true;
    }

    public boolean isPopulated() {
        return populated;
    }

    public void setPopulated() {
        this.populated = true;
    }

    /**
     * Whether the chunk has changed since it was last loaded or saved.
     *
     * @return dirty
     */
    public boolean isDirty() {
        return dirty;
    }

    /**
     * Refreshes chunk's dirty status.
     */
    private void refresh() {
        this.dirty = false;
    }

    public void setDirty() {
        if (!this.dirty) {
            this.dirty = true;
            this.clearCache();
        }
    }

    private synchronized void clearCache() {
        // Clear cached packet
        if (this.cached != null) {
            LevelChunkPacket packet = this.cached.get();
            if (packet != null) {
                packet.release();
            }
            this.cached = null;
        }
    }

    public synchronized void tick(int tick) {

    }

    /**
     * Clear chunk to a state as if it was not generated.
     */
    public synchronized void clear() {
        Arrays.fill(this.sections, null);
        Arrays.fill(this.biomes, (byte) 0);
        Arrays.fill(this.heightMap, (byte) 0);
        this.extraData.clear();
        this.blockUpdates.clear();
        this.tiles.clear();
        this.entities.clear();
        this.generated = false;
        this.chunkDataLoaders = null;
        this.clearCache();
    }

    @Override
    public void close() {
        for (Entity entity : this.entities) {
            if (entity instanceof Player) {
                continue;
            }
            entity.close();
        }

        this.tiles.forEachValue(blockEntity -> {
            blockEntity.close();
            return false;
        });

        this.clearCache();
    }

    private static class CacheSoftReference extends FinalizableSoftReference<LevelChunkPacket> {
        private final LevelChunkPacket hardRef;

        /**
         * Constructs a new finalizable soft reference.
         *
         * @param referent to softly reference
         * @param queue    that should finalize the referent
         */
        private CacheSoftReference(LevelChunkPacket referent, FinalizableReferenceQueue queue) {
            super(referent, queue);
            this.hardRef = referent;
        }

        @Override
        public void finalizeReferent() {
            this.hardRef.release();
        }
    }
}
