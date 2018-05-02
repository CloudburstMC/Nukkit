package com.nukkitx.api.event.block;

import com.nukkitx.api.block.Block;
import com.nukkitx.api.event.Cancellable;

public class BlockUpdateEvent implements BlockEvent, Cancellable {
    private final Block block;
    private boolean cancelled;

    public BlockUpdateEvent(Block block) {
        this.block = block;
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
        return block;
    }
}
