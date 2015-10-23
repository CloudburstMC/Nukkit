package cn.nukkit.event.entity;

import cn.nukkit.entity.Projectile;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class ProjectileLaunchEvent extends EntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public ProjectileLaunchEvent(Projectile entity) {
        this.entity = entity;
    }

    public Projectile getEntity() {
        return (Projectile) this.entity;
    }
}
