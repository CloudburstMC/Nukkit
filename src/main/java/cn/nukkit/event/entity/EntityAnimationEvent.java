package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class EntityAnimationEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final AnimationType animationType;

    public EntityAnimationEvent(Entity entity, AnimationType animation) {
        this.entity = entity;
        this.animationType = animation;
    }

    public AnimationType getAnimationType() {
        return this.animationType;
    }

    public enum AnimationType {
        HURT,
        DEATH
    }
}
