package cn.nukkit.server.event.entity;

import cn.nukkit.server.entity.Entity;
import cn.nukkit.server.entity.EntityCreature;
import cn.nukkit.server.entity.EntityHuman;
import cn.nukkit.server.entity.item.EntityItem;
import cn.nukkit.server.entity.item.EntityVehicle;
import cn.nukkit.server.entity.projectile.EntityProjectile;
import cn.nukkit.server.event.HandlerList;
import cn.nukkit.server.level.Position;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EntityDespawnEvent extends EntityEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final int entityType;

    public EntityDespawnEvent(Entity entity) {
        this.entity = entity;
        this.entityType = entity.getNetworkId();
    }

    public Position getPosition() {
        return this.entity.getPosition();
    }

    public int getType() {
        return this.entityType;
    }

    public boolean isCreature() {
        return this.entity instanceof EntityCreature;
    }

    public boolean isHuman() {
        return this.entity instanceof EntityHuman;
    }

    public boolean isProjectile() {
        return this.entity instanceof EntityProjectile;
    }

    public boolean isVehicle() {
        return this.entity instanceof EntityVehicle;
    }

    public boolean isItem() {
        return this.entity instanceof EntityItem;
    }

}
