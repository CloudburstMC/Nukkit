package cn.nukkit.event.vehicle;

import cn.nukkit.entity.item.EntityVehicle;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

/**
 * Is called when an entity takes damage
 */
public class VehicleDamageEvent extends VehicleEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private double damage;

    /**
     * Constructor for the VehicleDamageEvent
     *
     * @param vehicle the damaged vehicle
     * @param damage  the caused damage on the vehicle
     */

    public VehicleDamageEvent(final EntityVehicle vehicle, final double damage) {
        super(vehicle);

        this.damage = damage;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Returns the caused damage on the vehicle
     *
     * @return caused damage on the vehicle
     */

    public double getDamage() {
        return damage;
    }

    /**
     * Sets the damage caused on the vehicle
     *
     * @param damage the caused damage
     */

    public void setDamage(final double damage) {
        this.damage = damage;
    }
}
