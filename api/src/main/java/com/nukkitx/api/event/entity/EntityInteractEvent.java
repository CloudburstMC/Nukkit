package com.nukkitx.api.event.entity;

import com.nukkitx.api.block.Block;
import com.nukkitx.api.entity.Entity;
import com.nukkitx.api.event.Cancellable;

public class EntityInteractEvent implements EntityEvent, Cancellable {
    private final Entity entity;
    private final Block block;
    private boolean cancelled;

    public EntityInteractEvent(Entity entity, Block block) {
        this.entity = entity;
        this.block = block;
    }

    public Block getBlock() {
        return block;
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
