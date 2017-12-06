package cn.nukkit.api.event.block;

import cn.nukkit.server.block.Block;
import cn.nukkit.server.event.Cancellable;
import cn.nukkit.server.event.HandlerList;

public class BlockFromToEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Block to;

    public BlockFromToEvent(Block block, Block to) {
        super(block);
        this.to = to;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Block getTo() {
        return to;
    }
}