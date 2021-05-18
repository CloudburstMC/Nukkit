package cn.nukkit.event.world;

import cn.nukkit.event.HandlerList;
import cn.nukkit.world.format.FullChunk;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ChunkPopulateEvent extends ChunkEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public ChunkPopulateEvent(FullChunk chunk) {
        super(chunk);
    }

}