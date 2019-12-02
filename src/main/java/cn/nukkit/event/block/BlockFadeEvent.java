package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class BlockFadeEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Block newState;

    public BlockFadeEvent(Block block, Block newState) {
        super(block);
        this.newState = newState;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Block getNewState() {
        return newState;
    }
}
