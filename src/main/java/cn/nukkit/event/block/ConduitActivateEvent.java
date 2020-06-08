package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.event.HandlerList;

public class ConduitActivateEvent extends BlockEvent {

    private static final HandlerList handlers = new HandlerList();

    public ConduitActivateEvent(Block block) {
        super(block);
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

}
