package cn.nukkit.event.entity;

import cn.nukkit.entity.*;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Position;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EntityDespawnEvent extends EntityEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private int entityType;

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
