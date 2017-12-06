package cn.nukkit.api.event.entity;

import cn.nukkit.server.entity.projectile.EntityProjectile;
import cn.nukkit.server.event.Cancellable;
import cn.nukkit.server.event.HandlerList;
import cn.nukkit.server.level.MovingObjectPosition;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ProjectileHitEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private MovingObjectPosition movingObjectPosition;

    public ProjectileHitEvent(EntityProjectile entity) {
        this(entity, null);
    }

    public ProjectileHitEvent(EntityProjectile entity, MovingObjectPosition movingObjectPosition) {
        this.entity = entity;
        this.movingObjectPosition = movingObjectPosition;
    }

    public MovingObjectPosition getMovingObjectPosition() {
        return movingObjectPosition;
    }

    public void setMovingObjectPosition(MovingObjectPosition movingObjectPosition) {
        this.movingObjectPosition = movingObjectPosition;
    }

}
