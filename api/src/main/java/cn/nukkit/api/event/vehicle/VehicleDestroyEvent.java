package cn.nukkit.api.event.vehicle;

import cn.nukkit.api.entity.Entity;
import cn.nukkit.api.event.Cancellable;

public class VehicleDestroyEvent implements VehicleEvent, Cancellable {
    private final Entity vehicle;
    private final Entity attacker;
    private boolean cancelled;

    public VehicleDestroyEvent(Entity vehicle, Entity attacker) {
        this.vehicle = vehicle;
        this.attacker = attacker;
    }

    public Entity getAttacker() {
        return attacker;
    }

    @Override
    public Entity getVehicle() {
        return vehicle;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
