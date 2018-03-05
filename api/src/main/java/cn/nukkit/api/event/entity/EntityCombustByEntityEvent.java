package cn.nukkit.api.event.entity;

import cn.nukkit.api.entity.Entity;

public class EntityCombustByEntityEvent implements EntityCombustEvent {

    private final Entity entity;
    private final Entity combuster;
    private int duration;
    private boolean cancelled;

    public EntityCombustByEntityEvent(Entity combuster, Entity combustee, int duration) {
        this.entity = combustee;
        this.combuster = combuster;
        this.duration = duration;
    }

    public Entity getCombuster() {
        return combuster;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public void setDuration(int duration) {
        this.duration = duration;
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
