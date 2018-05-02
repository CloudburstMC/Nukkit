package com.nukkitx.api.event.vehicle;

import com.nukkitx.api.entity.Entity;
import com.nukkitx.api.event.Cancellable;

public class VehicleDestroyEvent implements VehicleEvent, Cancellable {
    private final Entity vehicle;
    private final Entity attacker;
    private boolean cancelled;

    public VehicleDestroyEvent(Entity vehicle, Entity attacker) {
        this.vehicle = vehicle;
        this.attacker = attacker;
    }

    public Entity getAttacker() {
        return attacker;
    }

    @Override
    public Entity getVehicle() {
        return vehicle;
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
