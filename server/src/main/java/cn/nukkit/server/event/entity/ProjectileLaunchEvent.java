package cn.nukkit.server.event.entity;

import cn.nukkit.server.entity.projectile.EntityProjectile;
import cn.nukkit.server.event.Cancellable;
import cn.nukkit.server.event.HandlerList;

public class ProjectileLaunchEvent extends EntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public ProjectileLaunchEvent(EntityProjectile entity) {
        this.entity = entity;
    }

    public EntityProjectile getEntity() {
        return (EntityProjectile) this.entity;
    }
}
