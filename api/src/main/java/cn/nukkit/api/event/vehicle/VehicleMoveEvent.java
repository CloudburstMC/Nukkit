package cn.nukkit.api.event.vehicle;

import cn.nukkit.server.entity.item.EntityVehicle;
import cn.nukkit.server.event.HandlerList;
import cn.nukkit.server.level.Location;

public class VehicleMoveEvent extends VehicleEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Location from;
    private final Location to;

    public VehicleMoveEvent(EntityVehicle vehicle, Location from, Location to) {
        super(vehicle);
        this.from = from;
        this.to = to;
    }

    public Location getFrom() {
        return from;
    }

    public Location getTo() {
        return to;
    }
}
