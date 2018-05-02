package com.nukkitx.api.event.entity;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.Entity;
import com.nukkitx.api.event.Cancellable;

public class EntityMotionEvent implements EntityEvent, Cancellable {
    private final Entity entity;
    private final Vector3f motion;
    private boolean cancelled;

    public EntityMotionEvent(Entity entity, Vector3f motion) {
        this.entity = entity;
        this.motion = motion;
    }

    public Vector3f getMotion() {
        return this.motion;
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
