package cn.nukkit.server.event.entity;

import cn.nukkit.server.entity.Entity;
import cn.nukkit.server.entity.item.EntityVehicle;
import cn.nukkit.server.event.Cancellable;
import cn.nukkit.server.event.HandlerList;

public class EntityVehicleExitEvent extends EntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final EntityVehicle vehicle;

    public EntityVehicleExitEvent(Entity entity, EntityVehicle vehicle) {
        this.entity = entity;
        this.vehicle = vehicle;
    }

    public EntityVehicle getVehicle() {
        return vehicle;
    }

}
