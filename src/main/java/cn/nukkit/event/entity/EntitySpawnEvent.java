package cn.nukkit.event.entity;

import cn.nukkit.entity.Creature;
import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.Human;
import cn.nukkit.entity.misc.DroppedItem;
import cn.nukkit.entity.projectile.Projectile;
import cn.nukkit.entity.vehicle.Vehicle;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Position;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EntitySpawnEvent extends EntityEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public EntitySpawnEvent(cn.nukkit.entity.Entity entity) {
        this.entity = entity;
    }

    public Position getPosition() {
        return this.entity.getPosition();
    }

    public EntityType<?> getType() {
        return this.entity.getType();
    }

    public boolean isCreature() {
        return this.entity instanceof Creature;
    }

    public boolean isHuman() {
        return this.entity instanceof Human;
    }

    public boolean isProjectile() {
        return this.entity instanceof Projectile;
    }

    public boolean isVehicle() {
        return this.entity instanceof Vehicle;
    }

    public boolean isItem() {
        return this.entity instanceof DroppedItem;
    }

}
