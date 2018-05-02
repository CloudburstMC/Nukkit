package com.nukkitx.api.event.level;

import com.nukkitx.api.level.chunk.Chunk;

public interface ChunkEvent extends LevelEvent {
    Chunk getChunk();
}
