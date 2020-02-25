package cn.nukkit.level.chunk;

import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.player.Player;
import cn.nukkit.utils.Identifier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

@NotThreadSafe
public final class LockableChunk implements IChunk, Lock {
    private final UnsafeChunk unsafe;
    private final Lock lock;

    LockableChunk(UnsafeChunk unsafe, Lock lock) {
        this.unsafe = unsafe;
        this.lock = lock;
    }

    @Override
    public void lock() {
        this.lock.lock();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        this.lock.lockInterruptibly();
    }

    @Override
    public boolean tryLock() {
        return this.lock.tryLock();
    }

    @Override
    public boolean tryLock(long time, @Nonnull TimeUnit unit) throws InterruptedException {
        return this.lock.tryLock(time, unit);
    }

    @Override
    public void unlock() {
        this.lock.unlock();
    }

    @Override
    public Condition newCondition() {
        return lock.newCondition();
    }

    @Nonnull
    @Override
    public ChunkSection getOrCreateSection(int y) {
        return unsafe.getOrCreateSection(y);
    }

    @Nullable
    @Override
    public ChunkSection getSection(int y) {
        return unsafe.getSection(y);
    }

    @Nonnull
    @Override
    public ChunkSection[] getSections() {
        ChunkSection[] sections = unsafe.getSections();
        return Arrays.copyOf(sections, sections.length);
    }

    @Nonnull
    @Override
    public Block getBlock(int x, int y, int z, int layer) {
        return unsafe.getBlock(x, y, z, layer);
    }

    @Nonnull
    @Override
    public Identifier getBlockId(int x, int y, int z, int layer) {
        return unsafe.getBlockId(x, y, z, layer);
    }

    @Override
    public int getBlockRuntimeIdUnsafe(int x, int y, int z, int layer) {
        return this.unsafe.getBlockRuntimeIdUnsafe(x, y, z, layer);
    }

    @Override
    public int getBlockData(int x, int y, int z, int layer) {
        return unsafe.getBlockData(x, y, z, layer);
    }

    @Override
    public Block getAndSetBlock(int x, int y, int z, int layer, Block block) {
        return unsafe.getAndSetBlock(x, y, z, layer, block);
    }

    @Override
    public void setBlock(int x, int y, int z, int layer, Block block) {
        this.unsafe.setBlock(x, y, z, layer, block);
    }

    @Override
    public void setBlockId(int x, int y, int z, int layer, Identifier id) {
        this.unsafe.setBlockId(x, y, z, layer, id);
    }

    @Override
    public void setBlockRuntimeIdUnsafe(int x, int y, int z, int layer, int runtimeId) {
        this.unsafe.setBlockRuntimeIdUnsafe(x, y, z, layer, runtimeId);
    }

    @Override
    public void setBlockData(int x, int y, int z, int layer, int data) {
        this.unsafe.setBlockData(x, y, z, layer, data);
    }

    @Override
    public int getBiome(int x, int z) {
        return this.unsafe.getBiome(x, z);
    }

    @Override
    public void setBiome(int x, int z, int biome) {
        this.unsafe.setBiome(x, z, biome);
    }

    @Override
    public byte getSkyLight(int x, int y, int z) {
        return this.unsafe.getSkyLight(x, y, z);
    }

    @Override
    public void setSkyLight(int x, int y, int z, int level) {
        this.unsafe.setSkyLight(x, y, z, level);
    }

    @Override
    public byte getBlockLight(int x, int y, int z) {
        return this.unsafe.getBlockLight(x, y, z);
    }

    @Override
    public void setBlockLight(int x, int y, int z, int level) {
        this.unsafe.setBlockLight(x, y, z, level);
    }

    @Override
    public int getHeightMap(int x, int z) {
        return this.unsafe.getHeightMap(x, z);
    }

    @Override
    public void setHeightMap(int x, int z, int value) {
        this.unsafe.setHeightMap(x, z, value);
    }

    @Override
    public void addEntity(@Nonnull Entity entity) {
        this.unsafe.addEntity(entity);
    }

    @Override
    public void removeEntity(Entity entity) {
        this.unsafe.removeEntity(entity);
    }

    @Override
    public void addBlockEntity(BlockEntity blockEntity) {
        this.unsafe.addBlockEntity(blockEntity);
    }

    @Override
    public void removeBlockEntity(BlockEntity blockEntity) {
        this.unsafe.removeBlockEntity(blockEntity);
    }

    @Override
    public BlockEntity getBlockEntity(int x, int y, int z) {
        return this.unsafe.getBlockEntity(x, y, z);
    }

    @Override
    public int getX() {
        return this.unsafe.getX();
    }

    @Override
    public int getZ() {
        return this.unsafe.getZ();
    }

    @Nonnull
    @Override
    public Level getLevel() {
        return this.unsafe.getLevel();
    }

    @Nonnull
    @Override
    public byte[] getBiomeArray() {
        byte[] biomes = this.unsafe.getBiomeArray();
        return Arrays.copyOf(biomes, biomes.length);
    }

    @Nonnull
    @Override
    public int[] getHeightMapArray() {
        int[] heightmap = this.unsafe.getHeightMapArray();
        return Arrays.copyOf(heightmap, heightmap.length);
    }

    @Nonnull
    @Override
    public Set<Player> getPlayers() {
        return this.unsafe.getPlayers();
    }

    @Nonnull
    @Override
    public Set<Entity> getEntities() {
        return this.unsafe.getEntities();
    }

    @Nonnull
    @Override
    public Collection<BlockEntity> getBlockEntities() {
        return this.unsafe.getBlockEntities();
    }

    @Override
    public boolean isGenerated() {
        return unsafe.isGenerated();
    }

    @Override
    public void setGenerated(boolean generated) {
        unsafe.setGenerated(generated);
    }

    @Override
    public boolean isPopulated() {
        return unsafe.isPopulated();
    }

    @Override
    public void setPopulated(boolean populated) {
        unsafe.setPopulated(populated);
    }

    @Override
    public boolean isDirty() {
        return this.unsafe.isDirty();
    }

    @Override
    public void setDirty(boolean dirty) {
        this.unsafe.setDirty(dirty);
    }

    @Override
    public boolean clearDirty() {
        return this.unsafe.clearDirty();
    }

    @Override
    public void clear() {
        this.unsafe.clear();
    }
}
