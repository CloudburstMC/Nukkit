package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.event.Event;

/**
 * Generic block event.
 * @author MagicDroidX
 */
public abstract class BlockEvent extends Event {

    protected final Block block;

    /**
     * Generic block event.
     * NOTICE: This event isn't meant to be called.
     * @param block Block.
     */
    public BlockEvent(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }
}
