package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

/**
 * Event called before checking nearby logs or making leaves decay.
 * @author MagicDroidX
 */
public class LeavesDecayEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Event for leaves decaying / disappearing.
     * @param block Leaves block.
     */
    public LeavesDecayEvent(Block block) {
        super(block);
    }
}
