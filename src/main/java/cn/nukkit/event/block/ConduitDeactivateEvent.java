package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.event.HandlerList;

public class ConduitDeactivateEvent extends BlockEvent {

    private static final HandlerList handlers = new HandlerList();

    public ConduitDeactivateEvent(Block block) {
        super(block);
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

}
