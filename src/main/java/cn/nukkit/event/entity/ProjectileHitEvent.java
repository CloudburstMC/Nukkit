package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.HandlerList;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ProjectileHitEvent extends EntityEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public ProjectileHitEvent(EntityProjectile entity) {
        this.entity = entity;
    }

    @Override
    public Entity getEntity() {
        return super.getEntity();
    }
}
