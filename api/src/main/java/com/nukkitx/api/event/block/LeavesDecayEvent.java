package com.nukkitx.api.event.block;

import com.nukkitx.api.block.Block;
import com.nukkitx.api.event.Cancellable;

public class LeavesDecayEvent implements BlockEvent, Cancellable {
    private final Block block;
    private boolean cancelled;

    public LeavesDecayEvent(Block block) {
        this.block = block;
    }

    @Override
    public Block getBlock() {
        return block;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
