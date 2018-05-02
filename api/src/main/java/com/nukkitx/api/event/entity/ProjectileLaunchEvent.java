package com.nukkitx.api.event.entity;

import com.nukkitx.api.entity.Entity;
import com.nukkitx.api.event.Cancellable;

public class ProjectileLaunchEvent implements EntityEvent, Cancellable {
    private final Entity entity;
    private boolean cancelled;

    public ProjectileLaunchEvent(Entity entity) {
        this.entity = entity;
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
