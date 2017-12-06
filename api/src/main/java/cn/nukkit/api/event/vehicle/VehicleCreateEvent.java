package cn.nukkit.api.event.vehicle;

import cn.nukkit.server.entity.item.EntityVehicle;
import cn.nukkit.server.event.Cancellable;
import cn.nukkit.server.event.HandlerList;

public class VehicleCreateEvent extends VehicleEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public VehicleCreateEvent(EntityVehicle vehicle) {
        super(vehicle);
    }

}
