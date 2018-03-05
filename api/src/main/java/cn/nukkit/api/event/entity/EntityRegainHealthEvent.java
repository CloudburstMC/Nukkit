package cn.nukkit.api.event.entity;

import cn.nukkit.api.entity.Entity;
import cn.nukkit.api.event.Cancellable;

public class EntityRegainHealthEvent implements EntityEvent, Cancellable {
    private final Entity entity;
    private float amount;
    private final Cause cause;
    private boolean cancelled;

    public EntityRegainHealthEvent(Entity entity, float amount, Cause cause) {
        this.entity = entity;
        this.amount = amount;
        this.cause = cause;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public Cause getRegainCause() {
        return cause;
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

    public enum Cause {
        REGENERATION,
        EATING,
        MAGIC,
        CUSTOM
    }
}
