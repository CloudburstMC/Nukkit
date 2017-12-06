package cn.nukkit.api.event.vehicle;

import cn.nukkit.api.Player;
import cn.nukkit.server.entity.Entity;
import cn.nukkit.server.entity.item.EntityVehicle;
import cn.nukkit.server.event.Cancellable;
import cn.nukkit.server.event.HandlerList;

public class EntityEnterVehicleEvent extends VehicleEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Entity riding;

    public EntityEnterVehicleEvent(Entity riding, EntityVehicle vehicle) {
        super(vehicle);
        this.riding = riding;
    }

    public Entity getEntity() {
        return riding;
    }

    public boolean isPlayer() {
        return riding instanceof Player;
    }

}
