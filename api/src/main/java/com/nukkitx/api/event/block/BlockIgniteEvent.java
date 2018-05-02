package com.nukkitx.api.event.block;

import com.nukkitx.api.block.Block;
import com.nukkitx.api.entity.Entity;
import com.nukkitx.api.event.Cancellable;

public class BlockIgniteEvent implements BlockEvent, Cancellable {
    private final Block block;
    private final Block source;
    private final Entity entity;
    private final BlockIgniteCause cause;
    private boolean cancelled;

    public BlockIgniteEvent(Block block, Block source, Entity entity, BlockIgniteCause cause) {
        this.block = block;
        this.source = source;
        this.entity = entity;
        this.cause = cause;
    }

    public Block getSource() {
        return source;
    }

    public Entity getEntity() {
        return entity;
    }

    public BlockIgniteCause getCause() {
        return cause;
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

    public enum BlockIgniteCause {
        EXPLOSION,
        FIREBALL,
        FLINT_AND_STEEL,
        LAVA,
        LIGHTNING,
        SPREAD
    }
}
