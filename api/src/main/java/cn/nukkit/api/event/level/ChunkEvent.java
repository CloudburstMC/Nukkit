package cn.nukkit.api.event.level;

import cn.nukkit.api.level.chunk.Chunk;

public interface ChunkEvent extends LevelEvent {
    Chunk getChunk();
}
