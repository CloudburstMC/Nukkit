package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class EntityVehicleExitEvent extends EntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Entity vehicle;

    public EntityVehicleExitEvent(Entity entity, Entity vehicle) {
        this.entity = entity;
        this.vehicle = vehicle;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Entity getVehicle() {
        return vehicle;
    }

}
