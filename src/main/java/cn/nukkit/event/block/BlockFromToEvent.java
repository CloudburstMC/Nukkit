package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

/**
 * Event for Block change "From To".
 */
public class BlockFromToEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Block to;

    /**
     * Event called on block changed from one type to another. E.g Redstone.
     * @param block Block that is being replaced.
     * @param to The replacement Block
     */
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