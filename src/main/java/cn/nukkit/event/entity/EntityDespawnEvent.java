package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.impl.EntityCreature;
import cn.nukkit.entity.impl.Human;
import cn.nukkit.entity.impl.projectile.EntityProjectile;
import cn.nukkit.entity.impl.vehicle.EntityVehicle;
import cn.nukkit.entity.misc.DroppedItem;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Location;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EntityDespawnEvent extends EntityEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public EntityDespawnEvent(Entity entity) {
        this.entity = entity;
    }

    public Location getLocation() {
        return this.entity.getLocation();
    }

    public EntityType<?> getType() {
        return this.entity.getType();
    }

    public boolean isCreature() {
        return this.entity instanceof EntityCreature;
    }

    public boolean isHuman() {
        return this.entity instanceof Human;
    }

    public boolean isProjectile() {
        return this.entity instanceof EntityProjectile;
    }

    public boolean isVehicle() {
        return this.entity instanceof EntityVehicle;
    }

    public boolean isItem() {
        return this.entity instanceof DroppedItem;
    }

}
