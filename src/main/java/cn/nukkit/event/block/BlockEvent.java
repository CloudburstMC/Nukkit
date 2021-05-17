package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.event.Event;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class BlockEvent extends Event {

    protected final Block block;

    public BlockEvent(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }
}
