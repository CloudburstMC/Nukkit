package cn.nukkit.api.event.entity;

import cn.nukkit.api.entity.Entity;
import cn.nukkit.api.event.Cancellable;
import com.flowpowered.math.vector.Vector3f;

public class ProjectileHitEvent implements EntityEvent, Cancellable {
    private final Entity entity;
    private Vector3f movingObjectPosition;
    private boolean cancelled;

    public ProjectileHitEvent(Entity entity) {
        this(entity, null);
    }

    public ProjectileHitEvent(Entity entity, Vector3f movingObjectPosition) {
        this.entity = entity;
        this.movingObjectPosition = movingObjectPosition;
    }

    public Vector3f getMovingObjectPosition() {
        return movingObjectPosition;
    }

    public void setMovingObjectPosition(Vector3f movingObjectPosition) {
        this.movingObjectPosition = movingObjectPosition;
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
