package cn.nukkit.event.entity;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

/**
 * @since 15-10-27
 */
public class EntityExplosionPrimeEvent extends EntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private double force;
    private boolean blockBreaking;
    private double fireChance;

    public EntityExplosionPrimeEvent(Entity entity, double force) {
        this.entity = entity;
        this.force = force;
        this.blockBreaking = true;
    }

    public double getForce() {
        return force;
    }

    public void setForce(double force) {
        this.force = force;
    }

    public boolean isBlockBreaking() {
        return blockBreaking;
    }

    public void setBlockBreaking(boolean blockBreaking) {
        this.blockBreaking = blockBreaking;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isIncendiary() {
        return fireChance > 0;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setIncendiary(boolean incendiary) {
        if (!incendiary) {
            fireChance = 0;
        } else if (fireChance <= 0) {
            fireChance = 1.0/3.0;
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public double getFireChance() {
        return fireChance;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setFireChance(double fireChance) {
        this.fireChance = fireChance;
    }
}
