package cn.nukkit.event.entity;

import cn.nukkit.entity.impl.projectile.EntityProjectile;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.MovingObjectPosition;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ProjectileHitEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private MovingObjectPosition movingObjectPosition;

    public ProjectileHitEvent(EntityProjectile entity) {
        this(entity, null);
    }

    public ProjectileHitEvent(EntityProjectile entity, MovingObjectPosition movingObjectPosition) {
        this.entity = entity;
        this.movingObjectPosition = movingObjectPosition;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public MovingObjectPosition getMovingObjectPosition() {
        return movingObjectPosition;
    }

    public void setMovingObjectPosition(MovingObjectPosition movingObjectPosition) {
        this.movingObjectPosition = movingObjectPosition;
    }

}
