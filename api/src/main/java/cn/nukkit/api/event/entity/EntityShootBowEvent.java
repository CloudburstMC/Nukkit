package cn.nukkit.api.event.entity;

import cn.nukkit.api.entity.Entity;
import cn.nukkit.api.entity.component.Projectile;
import cn.nukkit.api.event.Cancellable;
import cn.nukkit.api.item.ItemInstance;
import com.google.common.base.Preconditions;

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
