package com.nukkitx.server.level.feature;

import com.flowpowered.math.vector.Vector3i;
import com.nukkitx.api.block.Block;
import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.block.BlockType;
import com.nukkitx.api.block.BlockTypes;
import com.nukkitx.api.entity.Entity;
import com.nukkitx.api.level.Level;
import com.nukkitx.server.level.WorldChangeTransaction;

import java.util.Optional;
import java.util.Random;

public abstract class Feature {
    private final Entity entity;
    private WorldChangeTransaction transaction;

    protected Feature(WorldChangeTransaction transaction) {
        this(transaction, null);
    }

    protected Feature(WorldChangeTransaction transaction, Entity entity) {
        this.transaction = transaction;
        this.entity = entity;
    }

    public Optional<Entity> getEntity() {
        return Optional.ofNullable(entity);
    }

    public Optional<WorldChangeTransaction> getTransaction() {
        return Optional.ofNullable(transaction);
    }

    protected void placeBlock(Level level, Vector3i position, BlockState state) {
        if (transaction != null) {
            transaction.setBlock(position, state);
        } else {
            level.getChunkIfLoaded(position.getX() << 4, position.getZ() << 4).ifPresent(chunk ->
                    chunk.setBlock(position.getX(), position.getY(), position.getZ(), state));
        }
    }

    protected Optional<Block> getBlock(Level level, Vector3i position) {
        if (transaction != null) {
            return transaction.getBlock(position);
        } else {
            return level.getBlockIfChunkLoaded(position);
        }
    }

    protected boolean isEmptyBlock(Level level, Vector3i position) {
        Optional<Block> block = getBlock(level, position);
        return block.filter(block1 -> block1.getBlockState().getBlockType() == BlockTypes.AIR).isPresent();
    }

    protected Optional<BlockType> getBlockType(Level level, Vector3i position) {
        return getBlock(level, position).map(block -> block.getBlockState().getBlockType());
    }

    public abstract boolean place(Level level, Vector3i position, Random random);
}
