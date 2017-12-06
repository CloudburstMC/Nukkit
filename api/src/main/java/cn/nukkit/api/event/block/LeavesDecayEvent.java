package cn.nukkit.api.event.block;

import cn.nukkit.server.block.Block;
import cn.nukkit.server.event.Cancellable;
import cn.nukkit.server.event.HandlerList;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class LeavesDecayEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public LeavesDecayEvent(Block block) {
        super(block);
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

}
