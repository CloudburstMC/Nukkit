package com.nukkitx.api.event.block;

import com.nukkitx.api.block.Block;

public class BlockFormEvent implements BlockGrowEvent {
    private final Block oldBlock;
    private final Block newBlock;
    private boolean cancelled;

    public BlockFormEvent(Block oldBlock, Block newBlock) {
        this.oldBlock = oldBlock;
        this.newBlock = newBlock;
    }

    @Override
    public Block getNewState() {
        return newBlock;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public Block getBlock() {
        return oldBlock;
    }
}
