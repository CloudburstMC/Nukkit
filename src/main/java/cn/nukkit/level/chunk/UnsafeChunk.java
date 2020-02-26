package cn.nukkit.level.chunk;

import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.player.Player;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.utils.Identifier;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Closeable;
import java.util.*;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import static cn.nukkit.block.BlockIds.AIR;
import static cn.nukkit.level.chunk.Chunk.ARRAY_SIZE;
import static cn.nukkit.level.chunk.Chunk.SECTION_COUNT;
import static com.google.common.base.Preconditions.checkElementIndex;

public final class UnsafeChunk implements IChunk, Closeable {

    private static final AtomicIntegerFieldUpdater<UnsafeChunk> DIRTY_FIELD = AtomicIntegerFieldUpdater
            .newUpdater(UnsafeChunk.class, "dirty");
    private static final AtomicIntegerFieldUpdater<UnsafeChunk> INITIALIZED_FIELD = AtomicIntegerFieldUpdater
            .newUpdater(UnsafeChunk.class, "initialized");
    private static final AtomicIntegerFieldUpdater<UnsafeChunk> GENERATED_FIELD = AtomicIntegerFieldUpdater
            .newUpdater(UnsafeChunk.class, "generated");
    private static final AtomicIntegerFieldUpdater<UnsafeChunk> POPULATED_FIELD = AtomicIntegerFieldUpdater
            .newUpdater(UnsafeChunk.class, "populated");
    private static final AtomicIntegerFieldUpdater<UnsafeChunk> CLOSED_FIELD = AtomicIntegerFieldUpdater
            .newUpdater(UnsafeChunk.class, "closed");
    static final AtomicIntegerFieldUpdater<UnsafeChunk> CLEAR_CACHE_FIELD = AtomicIntegerFieldUpdater
            .newUpdater(UnsafeChunk.class, "clearCache");

    private final int x;

    private final int z;

    private final Level level;

    private final ChunkSection[] sections;

    private final Set<Player> players = Collections.newSetFromMap(new IdentityHashMap<>());

    private final Set<Entity> entities = Collections.newSetFromMap(new IdentityHashMap<>());

    private final Short2ObjectMap<BlockEntity> tiles = new Short2ObjectOpenHashMap<>();

    private final byte[] biomes;

    private final int[] heightMap;

    private volatile int dirty;

    private volatile int initialized;

    private volatile int generated;

    private volatile int populated;

    private volatile int closed;

    private volatile int clearCache;

    public UnsafeChunk(int x, int z, Level level) {
        this.x = x;
        this.z = z;
        this.level = level;
        this.sections = new ChunkSection[SECTION_COUNT];
        this.biomes = new byte[ARRAY_SIZE];
        this.heightMap = new int[ARRAY_SIZE];
    }

    UnsafeChunk(int x, int z, Level level, ChunkSection[] sections, byte[] biomes, int[] heightMap) {
        this.x = x;
        this.z = z;
        this.level = level;
        Preconditions.checkNotNull(sections, "sections");
        this.sections = Arrays.copyOf(sections, SECTION_COUNT);
        Preconditions.checkNotNull(biomes, "biomes");
        this.biomes = Arrays.copyOf(biomes, ARRAY_SIZE);
        Preconditions.checkNotNull(heightMap, "heightMap");
        this.heightMap = Arrays.copyOf(heightMap, ARRAY_SIZE);
    }

    static void checkBounds(int x, int y, int z) {
        checkElementIndex(y, 256, "y coordinate");
        checkBounds(x, z);
    }

    static void checkBounds(int x, int z) {
        checkElementIndex(x, 16, "x coordinate");
        checkElementIndex(z, 16, "z coordinate");
    }

    static int get2dIndex(int x, int z) {
        return z << 4 | x;
    }

    public boolean init() {
        return INITIALIZED_FIELD.compareAndSet(this, 0, 1);
    }


    @Nonnull
    @Override
    public ChunkSection getOrCreateSection(int y) {
        checkElementIndex(y, sections.length, "section Y");

        ChunkSection section = this.sections[y];
        if (section == null) {
            section = new ChunkSection();
            this.sections[y] = section;
            this.setDirty();
        }
        return section;
    }

    @Nullable
    @Override
    public synchronized ChunkSection getSection(int y) {
        checkElementIndex(y, sections.length, "section Y");
        return this.sections[y];
    }

    @Nonnull
    @Override
    public ChunkSection[] getSections() {
        return this.sections;
    }


    @Nonnull
    @Override
    public Block getBlock(int x, int y, int z, int layer) {
        checkBounds(x, y, z);
        ChunkSection section = this.getSection(y >> 4);
        Block block;
        if (section == null) {
            block = Block.get(AIR);
        } else {
            block = section.getBlock(x, y & 0xf, z, layer);
        }
        block.level = this.level;
        block.x = this.x << 4 | x & 0xf;
        block.y = y;
        block.z = this.z << 4 | z & 0xf;
        block.layer = layer;
        return block;
    }

    @Nonnull
    @Override
    public Identifier getBlockId(int x, int y, int z, int layer) {
        checkBounds(x, y, z);
        ChunkSection section = this.getSection(y >> 4);
        if (section == null) {
            return AIR;
        }
        return section.getBlockId(x, y & 0xf, z, layer);
    }

    @Override
    public int getBlockRuntimeIdUnsafe(int x, int y, int z, int layer) {
        checkBounds(x, y, z);
        ChunkSection section = this.getSection(y >> 4);
        if (section == null) {
            return BlockRegistry.get().getRuntimeId(AIR, 0); //will probably always evaluate to 0, but oh well
            //TODO: constant fields for runtime ids of default block states
        }
        return section.getBlockRuntimeIdUnsafe(x, y, z, layer);
    }

    @Override
    public int getBlockData(int x, int y, int z, int layer) {
        checkBounds(x, y, z);
        ChunkSection section = this.getSection(y >> 4);
        if (section == null) {
            return 0;
        }
        return section.getBlockData(x, y & 0xf, z, layer);
    }

    @Nonnull
    @Override
    public Block getAndSetBlock(int x, int y, int z, int layer, Block block) {
        Block previousBlock = this.getBlock(x, y, z, layer);
        this.setBlock(x, y, z, layer, block);
        return previousBlock;
    }

    @Override
    public void setBlock(int x, int y, int z, int layer, Block block) {
        checkBounds(x, y, z);
        ChunkSection section = this.getSection(y >> 4);
        if (section == null) {
            if (block.getId() == AIR) {
                // Setting air in an empty section.
                return;
            }
            section = this.getOrCreateSection(y >> 4);
        }

        section.setBlock(x, y & 0xf, z, layer, block);
        this.setDirty();
    }

    @Override
    public void setBlockId(int x, int y, int z, int layer, Identifier id) {
        this.setBlock(x, y, z, layer, BlockRegistry.get().getBlock(id, 0));
    }

    @Override
    public void setBlockRuntimeIdUnsafe(int x, int y, int z, int layer, int runtimeId) {
        checkBounds(x, y, z);
        ChunkSection section = this.getSection(y >> 4);
        if (section == null) {
            if (runtimeId == BlockRegistry.get().getRuntimeId(AIR, 0)) { //will probably always evaluate to 0, but oh well
                // Setting air in an empty section.
                return;
            }
            section = this.getOrCreateSection(y >> 4);
        }

        section.setBlockRuntimeIdUnsafe(x, y & 0xf, z, layer, runtimeId);
        this.setDirty();
    }

    @Override
    public void setBlockData(int x, int y, int z, int layer, int data) {
        checkBounds(x, y, z);
        ChunkSection section = this.getSection(y >> 4);
        if (section == null) {
            if (data == 0) {
                // Setting air in an empty section.
                return;
            }
            section = this.getOrCreateSection(y >> 4);
        }

        section.setBlockData(x, y & 0xf, z, layer, data);
        this.setDirty();
    }

    @Override
    public int getBiome(int x, int z) {
        checkBounds(x, z);
        return this.biomes[get2dIndex(x, z)] & 0xFF;
    }

    @Override
    public void setBiome(int x, int z, int biome) {
        checkBounds(x, z);
        int index = get2dIndex(x, z);
        int oldBiome = this.biomes[index] & 0xf;
        if (oldBiome != biome) {
            this.biomes[index] = (byte) biome;
            this.setDirty();
        }
    }

    @Override
    public byte getSkyLight(int x, int y, int z) {
        checkBounds(x, y, z);
        ChunkSection section = this.getSection(y >> 4);
        return section == null ? 0 : section.getSkyLight(x, y, z);
    }

    @Override
    public void setSkyLight(int x, int y, int z, int level) {
        checkBounds(x, y, z);
        this.getOrCreateSection(y >> 4).setSkyLight(x, y & 0xf, z, (byte) level);
        setDirty();
    }

    @Override
    public byte getBlockLight(int x, int y, int z) {
        checkBounds(x, y, z);
        ChunkSection section = this.getSection(y >> 4);
        return section == null ? 0 : section.getBlockLight(x, y & 0xf, z);
    }

    @Override
    public void setBlockLight(int x, int y, int z, int level) {
        checkBounds(x, y, z);
        this.getOrCreateSection(y >> 4).setBlockLight(x, y & 0xf, z, (byte) level);
        setDirty();
    }

    @Override
    public int getHeightMap(int x, int z) {
        checkBounds(x, z);
        return this.heightMap[get2dIndex(x, z)] & 0xFF;
    }

    @Override
    public void setHeightMap(int x, int z, int value) {
        checkBounds(x, z);
        this.heightMap[get2dIndex(x, z)] = (byte) value;
        setDirty();
    }

    @Override
    public void addEntity(@Nonnull Entity entity) {
        Preconditions.checkNotNull(entity, "entity");
        if (entity instanceof Player) {
            this.players.add((Player) entity);
        } else if (this.entities.add(entity) && this.initialized == 1) {
            this.setDirty();
        }
    }

    @Override
    public void removeEntity(Entity entity) {
        Preconditions.checkNotNull(entity, "entity");
        if (entity instanceof Player) {
            this.players.remove(entity);
        } else if (this.entities.remove(entity) && this.initialized == 1) {
            this.setDirty();
        }
    }

    @Override
    public void addBlockEntity(BlockEntity blockEntity) {
        Preconditions.checkNotNull(blockEntity, "blockEntity");
        short hash = Chunk.blockKey(blockEntity);
        if (this.tiles.put(hash, blockEntity) != blockEntity && this.initialized == 1) {
            this.setDirty();
        }
    }

    @Override
    public void removeBlockEntity(BlockEntity blockEntity) {
        Preconditions.checkNotNull(blockEntity, "blockEntity");
        short hash = Chunk.blockKey(blockEntity);
        if (this.tiles.remove(hash) == blockEntity && this.initialized == 1) {
            this.setDirty();
        }
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(int x, int y, int z) {
        checkBounds(x, y, z);
        return this.tiles.get(Chunk.blockKey(x, y, z));
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getZ() {
        return z;
    }

    @Nonnull
    @Override
    public Level getLevel() {
        return level;
    }

    @Nonnull
    @Override
    public byte[] getBiomeArray() {
        return biomes;
    }

    @Nonnull
    @Override
    public int[] getHeightMapArray() {
        return heightMap;
    }


    /**
     * Gets an immutable copy of players currently in this chunk
     *
     * @return player set
     */
    @Nonnull
    @Override
    public Set<Player> getPlayers() {
        return players;
    }

    /**
     * Gets an immutable copy of entities currently in this chunk
     *
     * @return entity set
     */
    @Nonnull
    @Override
    public Set<Entity> getEntities() {
        return this.entities;
    }

    /**
     * Gets an immutable copy of all block entities within the current chunk.
     *
     * @return block entity collection
     */
    @Nonnull
    @Override
    public Collection<BlockEntity> getBlockEntities() {
        return this.tiles.values();
    }

    public boolean isGenerated() {
        return generated == 1;
    }

    public void setGenerated(boolean generated) {
        if (GENERATED_FIELD.compareAndSet(this, generated ? 0 : 1, generated ? 1 : 0)) {
            setDirty();
        }
    }

    public void setGenerated() {
        this.setGenerated(true);
    }

    public boolean isPopulated() {
        return populated == 1;
    }

    public void setPopulated(boolean populated) {
        if (POPULATED_FIELD.compareAndSet(this, populated ? 0 : 1, populated ? 1 : 0)) {
            setDirty();
        }
    }

    public void setPopulated() {
        this.setPopulated(true);
    }

    /**
     * Whether the chunk has changed since it was last loaded or saved.
     *
     * @return dirty
     */
    public boolean isDirty() {
        return dirty == 1;
    }

    /**
     * Sets the chunk's dirty status.
     */
    public void setDirty(boolean dirty) {
        if (dirty) {
            CLEAR_CACHE_FIELD.set(this, 1);
        }
        DIRTY_FIELD.set(this, dirty ? 1 : 0);
    }

    @Override
    public boolean clearDirty() {
        return DIRTY_FIELD.compareAndSet(this, 1, 0);
    }

    /**
     * Clear chunk to a state as if it was not generated.
     */
    public synchronized void clear() {
        Arrays.fill(this.sections, null);
        Arrays.fill(this.biomes, (byte) 0);
        Arrays.fill(this.heightMap, (byte) 0);
        this.tiles.clear();
        this.entities.clear();
        this.generated = 0;
        this.populated = 0;
        this.dirty = 1;
    }

    @Override
    public synchronized void close() {
        if (CLOSED_FIELD.compareAndSet(this, 0, 1)) {
            for (Entity entity : ImmutableList.copyOf(this.entities)) {
                if (entity instanceof Player) {
                    continue;
                }
                entity.close();
            }

            this.tiles.values().forEach(BlockEntity::close);
            clear();
        }
    }
}
