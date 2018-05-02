package com.nukkitx.api.event.entity;

import com.nukkitx.api.block.Block;
import com.nukkitx.api.entity.Entity;
import com.nukkitx.api.event.Cancellable;

public class EntityBlockChangeEvent implements EntityEvent, Cancellable {
    private final Entity entity;
    private final Block from;
    private final Block to;
    private boolean cancelled;

    public EntityBlockChangeEvent(Entity entity, Block from, Block to) {
        this.entity = entity;
        this.from = from;
        this.to = to;
    }

    public Block getFrom() {
        return from;
    }

    public Block getTo() {
        return to;
    }

    @Override
    public Entity getEntity() {
        return entity;
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
