package com.nukkitx.api.event.entity;

import com.nukkitx.api.entity.Entity;
import com.nukkitx.api.event.Cancellable;

public class EntityExplosionPrimeEvent implements EntityEvent, Cancellable {
    private final Entity entity;
    private float force;
    private boolean blockBreaking;
    private boolean cancelled;

    public EntityExplosionPrimeEvent(Entity entity, float force) {
        this.entity = entity;
        this.force = force;
        this.blockBreaking = true;
    }

    public float getForce() {
        return force;
    }

    public boolean isBlockBreaking() {
        return blockBreaking;
    }

    public void setBlockBreaking(boolean blockBreaking) {
        this.blockBreaking = blockBreaking;
    }

    public void setForce(float force) {
        this.force = force;
    }

    @Override
    public Entity getEntity() {
        return entity;
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
