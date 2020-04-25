package cn.nukkit.event.vehicle;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.player.Player;

public class EntityEnterVehicleEvent extends VehicleEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Entity riding;

    public EntityEnterVehicleEvent(Entity riding, Entity vehicle) {
        super(vehicle);
        this.riding = riding;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Entity getEntity() {
        return riding;
    }

    public boolean isPlayer() {
        return riding instanceof Player;
    }

}
