package cn.nukkit.api.event.vehicle;

import cn.nukkit.api.entity.Entity;
import cn.nukkit.api.event.Cancellable;

public class VehicleCreateEvent implements VehicleEvent, Cancellable {
    private final Entity vehicle;
    private boolean cancelled;

    public VehicleCreateEvent(Entity vehicle) {
        this.vehicle = vehicle;
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
