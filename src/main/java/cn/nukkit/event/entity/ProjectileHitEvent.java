package cn.nukkit.event.entity;

import cn.nukkit.entity.projectile.Projectile;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.MovingObjectPosition;

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

    public ProjectileHitEvent(Projectile entity) {
        this(entity, null);
    }

    public ProjectileHitEvent(Projectile entity, MovingObjectPosition movingObjectPosition) {
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
