package cn.nukkit.event.vehicle;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class VehicleDestroyEvent extends VehicleEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Entity attacker;

    public VehicleDestroyEvent(Entity vehicle, Entity attacker) {
        super(vehicle);
        this.attacker = attacker;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Entity getAttacker() {
        return attacker;
    }

}
