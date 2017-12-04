package cn.nukkit.server.event.vehicle;

import cn.nukkit.server.entity.Entity;
import cn.nukkit.server.entity.item.EntityVehicle;
import cn.nukkit.server.event.Cancellable;
import cn.nukkit.server.event.HandlerList;

public class VehicleDamageEvent extends VehicleEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Entity attacker;
    private double damage;

    public VehicleDamageEvent(EntityVehicle vehicle, Entity attacker, double damage) {
        super(vehicle);
        this.attacker = attacker;
        this.damage = damage;
    }

    public Entity getAttacker() {
        return attacker;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

}
