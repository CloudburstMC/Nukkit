package cn.nukkit.api.event.entity;

import cn.nukkit.server.entity.Entity;
import cn.nukkit.server.entity.EntityLiving;
import cn.nukkit.server.entity.projectile.EntityProjectile;
import cn.nukkit.server.event.Cancellable;
import cn.nukkit.server.event.HandlerList;
import cn.nukkit.server.item.Item;

/**
 * author: Box
 * Nukkit Project
 */
public class EntityShootBowEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Item bow;

    private EntityProjectile projectile;

    private double force;

    public EntityShootBowEvent(EntityLiving shooter, Item bow, EntityProjectile projectile, double force) {
        this.entity = shooter;
        this.bow = bow;
        this.projectile = projectile;
        this.force = force;
    }

    @Override
    public EntityLiving getEntity() {
        return (EntityLiving) this.entity;
    }


    public Item getBow() {
        return this.bow;
    }


    public EntityProjectile getProjectile() {
        return this.projectile;
    }

    public void setProjectile(Entity projectile) {
        if (projectile != this.projectile) {
            if (this.projectile.getViewers().size() == 0) {
                this.projectile.kill();
                this.projectile.close();
            }
            this.projectile = (EntityProjectile) projectile;
        }
    }

    public double getForce() {
        return this.force;
    }

    public void setForce(double force) {
        this.force = force;
    }
}
