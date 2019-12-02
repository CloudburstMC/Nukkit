package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockSpreadEvent extends BlockFormEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Block source;

    public BlockSpreadEvent(Block block, Block source, Block newState) {
        super(block, newState);
        this.source = source;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Block getSource() {
        return source;
    }
}
