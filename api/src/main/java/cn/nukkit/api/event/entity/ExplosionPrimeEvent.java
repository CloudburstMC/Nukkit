package cn.nukkit.api.event.entity;

import cn.nukkit.api.entity.Entity;
import cn.nukkit.api.event.Cancellable;

public class ExplosionPrimeEvent implements EntityEvent, Cancellable {
    private final Entity entity;
    protected float force;
    private boolean blockBreaking;
    private boolean cancelled;

    public ExplosionPrimeEvent(Entity entity, float force) {
        this.entity = entity;
        this.force = force;
        this.blockBreaking = true;
    }

    public float getForce() {
        return this.force;
    }

    public void setForce(float force) {
        this.force = force;
    }

    public boolean isBlockBreaking() {
        return this.blockBreaking;
    }

    public void setBlockBreaking(boolean affectsBlocks) {
        this.blockBreaking = affectsBlocks;
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
