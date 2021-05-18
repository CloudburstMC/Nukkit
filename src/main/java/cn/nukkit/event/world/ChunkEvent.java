package cn.nukkit.event.world;

import cn.nukkit.world.format.FullChunk;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class ChunkEvent extends WorldEvent {

    private final FullChunk chunk;

    public ChunkEvent(FullChunk chunk) {
        super(chunk.getProvider().getWorld());
        this.chunk = chunk;
    }

    public FullChunk getChunk() {
        return chunk;
    }
}
