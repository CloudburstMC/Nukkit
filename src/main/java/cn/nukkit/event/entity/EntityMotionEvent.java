package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import com.nukkitx.math.vector.Vector3f;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EntityMotionEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Vector3f motion;

    public EntityMotionEvent(Entity entity, Vector3f motion) {
        this.entity = entity;
        this.motion = motion;
    }

    public Vector3f getMotion() {
        return this.motion;
    }
}
