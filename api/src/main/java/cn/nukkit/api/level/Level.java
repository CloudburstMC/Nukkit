package cn.nukkit.api.level;

import cn.nukkit.api.block.Block;
import cn.nukkit.api.entity.Entity;
import cn.nukkit.api.entity.system.System;
import cn.nukkit.api.level.chunk.Chunk;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface Level {

    String getId();

    String getName();

    int getTime();

    long getCurrentTick();

    LevelData getData();

    Optional<Chunk> getChunkIfLoaded(int x, int z);

    CompletableFuture<Chunk> getChunk(int x, int z);

    void save();

    default CompletableFuture<Chunk> getChunkForPosition(Vector3i position) {
        return getChunk(position.getX() >> 4, position.getY() >> 4);
    }

    default CompletableFuture<Block> getBlock(@Nonnull Vector3i vector) {
        Preconditions.checkNotNull(vector, "vector");
        return getBlock(vector.getX(), vector.getY(), vector.getZ());
    }

    default CompletableFuture<Block> getBlock(int x, int y, int z) {
        return getChunk(x >> 4, z >> 4).thenApply(chunk -> chunk.getBlock(x & 0x0f, y, z & 0x0f));
    }

    default Optional<Block> getBlockIfChunkLoaded(@Nonnull Vector3i vector) {
        Preconditions.checkNotNull(vector, "vector");
        return getBlockIfChunkLoaded(vector.getX(), vector.getY(), vector.getZ());
    }

    default Optional<Block> getBlockIfChunkLoaded(int x, int y, int z) {
        Optional<Chunk> chunkOptional = getChunkIfLoaded(x >> 4, z >> 4);
        return chunkOptional.map(c -> c.getBlock(x & 0x0f, y, z & 0x0f));
    }

    <T extends Entity> CompletableFuture<T> spawn(Class<? extends Entity> clazz, Vector3f position);

    void registerSystem(System system);

    void deregisterSystem(System system);
}
