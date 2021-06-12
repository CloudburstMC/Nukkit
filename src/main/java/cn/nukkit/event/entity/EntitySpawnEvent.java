package cn.nukkit.event.entity;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Position;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Is cancellable only in PowerNukkit")
public class EntitySpawnEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final int entityType;

    public EntitySpawnEvent(cn.nukkit.entity.Entity entity) {
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
        return this.entity instanceof Entity;
    }

    public boolean isItem() {
        return this.entity instanceof EntityItem;
    }

}
