package cn.nukkit.api.event.level;

import cn.nukkit.server.event.HandlerList;
import cn.nukkit.server.level.format.FullChunk;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ChunkPopulateEvent extends ChunkEvent {

    private static final HandlerList handlers = new HandlerList();

    public ChunkPopulateEvent(FullChunk chunk) {
        super(chunk);
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

}