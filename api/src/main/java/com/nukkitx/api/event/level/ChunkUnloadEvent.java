package com.nukkitx.api.event.level;

import com.nukkitx.api.event.Cancellable;
import com.nukkitx.api.level.Level;
import com.nukkitx.api.level.chunk.Chunk;

public class ChunkUnloadEvent implements ChunkEvent, Cancellable {
    private final Chunk chunk;
    private boolean cancelled;

    public ChunkUnloadEvent(Chunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public Chunk getChunk() {
        return chunk;
    }

    @Override
    public Level getLevel() {
        return chunk.getLevel();
    }
}