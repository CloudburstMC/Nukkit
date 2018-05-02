package com.nukkitx.api.event.vehicle;

import com.nukkitx.api.entity.Entity;
import com.nukkitx.api.util.Location;

public class VehicleMoveEvent implements VehicleEvent {
    private final Entity vehicle;
    private final Location from;
    private final Location to;

    public VehicleMoveEvent(Entity vehicle, Location from, Location to) {
        this.vehicle = vehicle;
        this.from = from;
        this.to = to;
    }

    public Location getFrom() {
        return from;
    }

    public Location getTo() {
        return to;
    }

    @Override
    public Entity getVehicle() {
        return vehicle;
    }
}
