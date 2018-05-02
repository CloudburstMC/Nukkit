package com.nukkitx.api.event.entity;

import com.nukkitx.api.entity.Entity;
import com.nukkitx.api.event.Cancellable;
import com.nukkitx.api.util.Location;

public class EntityTeleportEvent implements EntityEvent, Cancellable {
    private final Entity entity;
    private final Location from;
    private Location to;
    private boolean cancelled;

    public EntityTeleportEvent(Entity entity, Location from, Location to) {
        this.entity = entity;
        this.from = from;
        this.to = to;
    }

    public Location getFrom() {
        return from;
    }

    public Location getTo() {
        return to;
    }

    public void setTo(Location to) {
        this.to = to;
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
