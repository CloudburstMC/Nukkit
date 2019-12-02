package cn.nukkit.event.level;

import cn.nukkit.event.HandlerList;
import cn.nukkit.level.format.FullChunk;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ChunkLoadEvent extends ChunkEvent {

    private static final HandlerList handlers = new HandlerList();
    private final boolean newChunk;

    public ChunkLoadEvent(FullChunk chunk, boolean newChunk) {
        super(chunk);
        this.newChunk = newChunk;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public boolean isNewChunk() {
        return newChunk;
    }
}