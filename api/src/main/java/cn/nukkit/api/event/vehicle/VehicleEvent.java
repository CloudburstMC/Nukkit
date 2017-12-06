package cn.nukkit.api.event.vehicle;

import cn.nukkit.server.entity.item.EntityVehicle;

/**
 * Created by larryTheCoder at 7/5/2017.
 * <p>
 * Nukkit Project
 */
public abstract class VehicleEvent extends Event {

    private final EntityVehicle vehicle;

    public VehicleEvent(EntityVehicle vehicle) {
        this.vehicle = vehicle;
    }

    public EntityVehicle getVehicle() {
        return vehicle;
    }
}
