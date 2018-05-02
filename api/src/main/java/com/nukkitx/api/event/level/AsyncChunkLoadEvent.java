package com.nukkitx.api.event.level;

import com.nukkitx.api.level.Level;
import com.nukkitx.api.level.chunk.Chunk;

public class AsyncChunkLoadEvent implements ChunkEvent {
    private final Chunk chunk;
    private final boolean newChunk;

    public AsyncChunkLoadEvent(Chunk chunk, boolean newChunk) {
        this.chunk = chunk;
        this.newChunk = newChunk;
    }

    public boolean isNewChunk() {
        return newChunk;
    }

    @Override
    public Chunk getChunk() {
        return null;
    }

    @Override
    public Level getLevel() {
        return null;
    }
}