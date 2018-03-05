package cn.nukkit.api.event.vehicle;

import cn.nukkit.api.Player;
import cn.nukkit.api.entity.Entity;
import cn.nukkit.api.event.Cancellable;

public class EntityEnterVehicleEvent implements VehicleEvent, Cancellable {
    private final Entity vehicle;
    private final Entity riding;
    private boolean cancelled;

    public EntityEnterVehicleEvent(Entity vehicle, Entity riding) {
        this.vehicle = vehicle;
        this.riding = riding;
    }

    public Entity getEntity() {
        return riding;
    }

    public boolean isPlayer() {
        return riding instanceof Player;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public Entity getVehicle() {
        return vehicle;
    }
}
