package com.nukkitx.server.block;

import com.flowpowered.math.vector.Vector3i;
import com.nukkitx.api.block.Block;
import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.level.Level;
import com.nukkitx.api.level.chunk.Chunk;
import com.nukkitx.api.util.BoundingBox;
import com.spotify.futures.CompletableFutures;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class NukkitBlock implements Block {
    private final Level level;
    private final Chunk chunk;
    private final Vector3i blockPosition;
    private final BlockState state;

    public NukkitBlock(Level level, Chunk chunk, Vector3i blockPosition, BlockState state) {
        this.level = level;
        this.chunk = chunk;
        this.blockPosition = blockPosition;
        this.state = state;
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public Chunk getChunk() {
        return chunk;
    }

    @Override
    public Vector3i getBlockPosition() {
        return blockPosition;
    }

    @Override
    public BoundingBox getBoundingBox() {
        return new BoundingBox(blockPosition.toFloat().floor(), blockPosition.toFloat().ceil());
    }

    @Override
    public BlockState getBlockState() {
        return state;
    }

    public List<Block> getNeighboringBlocksIfLoaded() {
        List<Block> blocks = new ArrayList<>();
        for (Vector3i position : BlockUtils.AROUND_POSITIONS) {
            level.getBlockIfChunkLoaded(blockPosition.add(position)).ifPresent(blocks::add);
        }
        return blocks;
    }

    public CompletableFuture<List<Block>> getNeighboringBlocks() {
        List<CompletableFuture<Block>> blockFutures = new ArrayList<>();
        for (Vector3i position : BlockUtils.AROUND_POSITIONS) {
            blockFutures.add(level.getBlock(blockPosition.add(position)));
        }

        return CompletableFutures.allAsList(blockFutures);
    }
}
