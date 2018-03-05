package cn.nukkit.api.event.level;

import cn.nukkit.api.level.Level;
import cn.nukkit.api.level.chunk.Chunk;

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