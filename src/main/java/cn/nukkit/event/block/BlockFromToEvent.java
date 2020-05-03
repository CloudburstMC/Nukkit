package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class BlockFromToEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Block to;

    public BlockFromToEvent(Block block, Block to) {
        super(block);
        this.to = to;
    }

    public Block getFrom() {
        return getBlock();
    }

    public Block getTo() {
        return to;
    }

    public void setTo(Block newTo) {
        to = newTo;
    }
}