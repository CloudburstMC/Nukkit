package cn.nukkit.server.event.vehicle;

import cn.nukkit.server.entity.item.EntityVehicle;
import cn.nukkit.server.event.HandlerList;

public class VehicleUpdateEvent extends VehicleEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public VehicleUpdateEvent(EntityVehicle vehicle) {
        super(vehicle);
    }

}
