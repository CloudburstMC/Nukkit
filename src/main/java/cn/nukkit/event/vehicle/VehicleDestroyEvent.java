package cn.nukkit.event.vehicle;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class VehicleDestroyEvent extends VehicleEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final cn.nukkit.entity.Entity attacker;

    public VehicleDestroyEvent(Entity vehicle, cn.nukkit.entity.Entity attacker) {
        super(vehicle);
        this.attacker = attacker;
    }

    public cn.nukkit.entity.Entity getAttacker() {
        return attacker;
    }

}
