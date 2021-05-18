package cn.nukkit.event.world;

import cn.nukkit.event.HandlerList;
import cn.nukkit.world.format.FullChunk;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ChunkLoadEvent extends ChunkEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final boolean newChunk;

    public ChunkLoadEvent(FullChunk chunk, boolean newChunk) {
        super(chunk);
        this.newChunk = newChunk;
    }

    public boolean isNewChunk() {
        return newChunk;
    }
}