package cn.nukkit.api.event.vehicle;

import cn.nukkit.server.entity.Entity;
import cn.nukkit.server.entity.item.EntityVehicle;
import cn.nukkit.server.event.Cancellable;
import cn.nukkit.server.event.HandlerList;

public class VehicleDestroyEvent extends VehicleEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Entity attacker;

    public VehicleDestroyEvent(EntityVehicle vehicle, Entity attacker) {
        super(vehicle);
        this.attacker = attacker;
    }

    public Entity getAttacker() {
        return attacker;
    }

}
