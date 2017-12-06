package cn.nukkit.api.event.block;

import cn.nukkit.server.block.Block;
import cn.nukkit.server.event.Cancellable;
import cn.nukkit.server.event.HandlerList;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockFormEvent extends BlockGrowEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public BlockFormEvent(Block block, Block newState) {
        super(block, newState);
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

}
