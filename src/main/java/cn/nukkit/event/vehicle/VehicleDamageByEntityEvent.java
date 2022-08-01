package cn.nukkit.event.vehicle;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityVehicle;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

/**
 * Is called when an entity damages a vehicle
 *
 * @author TrainmasterHD
 * @since 09.09.2019
 */
public final class VehicleDamageByEntityEvent extends VehicleDamageEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Entity attacker;

    /**
     * Constructor for the VehicleDamageByEntityEvent
     *
     * @param vehicle  the damaged vehicle
     * @param attacker the attacking vehicle
     * @param damage   the caused damage on the vehicle
     */

    public VehicleDamageByEntityEvent(final EntityVehicle vehicle, final Entity attacker, final double damage) {
        super(vehicle, damage);

        this.attacker = attacker;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Returns the attacking entity
     *
     * @return attacking entity
     */

    public Entity getAttacker() {
        return attacker;
    }
}
