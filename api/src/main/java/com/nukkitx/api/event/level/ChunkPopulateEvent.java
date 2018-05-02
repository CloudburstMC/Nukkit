package com.nukkitx.api.event.level;

import com.nukkitx.api.level.Level;
import com.nukkitx.api.level.chunk.Chunk;

public class ChunkPopulateEvent implements ChunkEvent {
    private final Chunk chunk;

    public ChunkPopulateEvent(Chunk chunk) {
        this.chunk = chunk;
    }


    @Override
    public Chunk getChunk() {
        return null;
    }

    @Override
    public Level getLevel() {
        return chunk.getLevel();
    }
}