package cn.nukkit.api.event.vehicle;

import cn.nukkit.api.entity.Entity;

public class VehicleUpdateEvent implements VehicleEvent {
    private final Entity vehicle;

    public VehicleUpdateEvent(Entity vehicle) {
        this.vehicle = vehicle;
    }

    @Override
    public Entity getVehicle() {
        return vehicle;
    }
}
