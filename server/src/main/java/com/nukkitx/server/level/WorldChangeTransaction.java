package com.nukkitx.server.level;

import com.flowpowered.math.vector.Vector3i;
import com.nukkitx.api.block.Block;
import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.level.Level;
import lombok.RequiredArgsConstructor;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class WorldChangeTransaction {
    private final Deque<Consumer<Level>> actions = new ArrayDeque<>();
    private final Level level;

    public void setBlock(Vector3i position, BlockState state) {
        actions.add(level1 -> level1.getChunkIfLoaded(position.getX() << 4, position.getZ() << 4).ifPresent(chunk ->
                chunk.setBlock(position.getX(), position.getY(), position.getZ(), state)));
    }

    public Optional<Block> getBlock(Vector3i position) {
        return level.getBlockIfChunkLoaded(position);
    }

    public void apply() {
        actions.forEach(consumer -> consumer.accept(level));
    }

    @FunctionalInterface
    private interface Action {
        void apply(Level level);
    }
}
