package com.nukkitx.api.block;

import com.flowpowered.math.vector.Vector3i;
import com.nukkitx.api.level.Level;
import com.nukkitx.api.level.chunk.Chunk;
import com.nukkitx.api.util.BoundingBox;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface Block extends BlockSnapshot {

    Level getLevel();

    Chunk getChunk();

    Vector3i getBlockPosition();

    default Vector3i getChunkLocation() {
        Vector3i level = getBlockPosition();
        return new Vector3i(level.getX() & 0x0f, level.getY(), level.getZ() & 0x0f);
    }

    BoundingBox getBoundingBox();

    List<Block> getNeighboringBlocksIfLoaded();

    CompletableFuture<List<Block>> getNeighboringBlocks();
}
