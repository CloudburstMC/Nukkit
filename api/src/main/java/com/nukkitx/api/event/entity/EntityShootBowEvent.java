package com.nukkitx.api.event.entity;

import com.google.common.base.Preconditions;
import com.nukkitx.api.entity.Entity;
import com.nukkitx.api.entity.component.Projectile;
import com.nukkitx.api.event.Cancellable;
import com.nukkitx.api.item.ItemInstance;

public class EntityShootBowEvent implements EntityEvent, Cancellable {
    private final Entity entity;
    private final ItemInstance bow;
    private Entity projectile;
    private float force;
    private boolean cancelled;

    public EntityShootBowEvent(Entity shooter, ItemInstance bow, Entity projectile, float force) {
        this.entity = shooter;
        this.bow = bow;
        this.projectile = projectile;
        this.force = force;
    }

    @Override
    public Entity getEntity() {
        return entity;
    }

    public ItemInstance getBow() {
        return bow;
    }

    public Entity getProjectile() {
        return projectile;
    }

    public void setProjectile(Entity projectile) {
        /*if (projectile != this.projectile) {
            if (this.projectile.getViewers().size() == 0) {
                this.projectile.kill();
                this.projectile.close();
            }
            this.projectile = projectile;
        }*/
        Preconditions.checkArgument(projectile.get(Projectile.class).isPresent(), "Entity is not a projectile");
        this.projectile = projectile;
    }

    public float getForce() {
        return this.force;
    }

    public void setForce(float force) {
        this.force = force;
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
