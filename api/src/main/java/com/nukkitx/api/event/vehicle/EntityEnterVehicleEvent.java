package com.nukkitx.api.event.vehicle;

import com.nukkitx.api.Player;
import com.nukkitx.api.entity.Entity;
import com.nukkitx.api.event.Cancellable;

public class EntityEnterVehicleEvent implements VehicleEvent, Cancellable {
    private final Entity vehicle;
    private final Entity riding;
    private boolean cancelled;

    public EntityEnterVehicleEvent(Entity vehicle, Entity riding) {
        this.vehicle = vehicle;
        this.riding = riding;
    }

    public Entity getEntity() {
        return riding;
    }

    public boolean isPlayer() {
        return riding instanceof Player;
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
    public Entity getVehicle() {
        return vehicle;
    }
}
