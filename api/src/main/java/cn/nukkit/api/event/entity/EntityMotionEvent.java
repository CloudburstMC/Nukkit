package cn.nukkit.api.event.entity;

import cn.nukkit.server.entity.Entity;
import cn.nukkit.server.event.Cancellable;
import cn.nukkit.server.event.HandlerList;
import cn.nukkit.server.math.Vector3;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EntityMotionEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Vector3 motion;

    public EntityMotionEvent(Entity entity, Vector3 motion) {
        this.entity = entity;
        this.motion = motion;
    }

    @Deprecated
    public Vector3 getVector() {
        return this.motion;
    }

    public Vector3 getMotion() {
        return this.motion;
    }
}
