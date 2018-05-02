package com.nukkitx.api.event.block;

import com.nukkitx.api.block.Block;
import com.nukkitx.api.event.Cancellable;

public class BlockFromToEvent implements BlockEvent, Cancellable {
    private final Block from;
    private final Block to;
    private boolean cancelled;

    public BlockFromToEvent(Block from, Block to) {
        this.from = from;
        this.to = to;
    }

    public Block getTo() {
        return to;
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
        return from;
    }
}