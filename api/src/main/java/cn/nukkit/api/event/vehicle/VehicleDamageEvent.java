package cn.nukkit.api.event.vehicle;

import cn.nukkit.api.entity.Entity;
import cn.nukkit.api.event.Cancellable;

public class VehicleDamageEvent implements VehicleEvent, Cancellable {
    private final Entity vehicle;
    private final Entity attacker;
    private float damage;
    private boolean cancelled;

    public VehicleDamageEvent(Entity vehicle, Entity attacker, float damage) {
        this.vehicle = vehicle;
        this.attacker = attacker;
        this.damage = damage;
    }

    public Entity getAttacker() {
        return attacker;
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
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
