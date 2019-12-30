package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class BlockCoralDeathEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private Block newState;

    public BlockCoralDeathEvent(Block block, Block newState) {
        super(block);
        this.newState = newState;
    }

    public Block getNewState() {
        return newState;
    }

    public void setNewState(Block newState) {
        this.newState = newState;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }
}
