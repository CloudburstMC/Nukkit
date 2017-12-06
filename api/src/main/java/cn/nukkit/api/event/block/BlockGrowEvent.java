package cn.nukkit.api.event.block;

import cn.nukkit.server.block.Block;
import cn.nukkit.server.event.Cancellable;
import cn.nukkit.server.event.HandlerList;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockGrowEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Block newState;

    public BlockGrowEvent(Block block, Block newState) {
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
