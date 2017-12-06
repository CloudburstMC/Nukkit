package cn.nukkit.api.event.block;

import cn.nukkit.server.block.Block;

/**
 * author: MagicDroidX
 * Nukkit Project
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
