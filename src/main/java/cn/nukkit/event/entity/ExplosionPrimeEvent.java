package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

/**
 * author: Box
 * Nukkit Project
 * <p>
 * Called when a entity decides to explode
 */
public class ExplosionPrimeEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    protected double force;
    private boolean blockBreaking;
    public ExplosionPrimeEvent(Entity entity, double force) {
        this.entity = entity;
        this.force = force;
        this.blockBreaking = true;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public double getForce() {
        return this.force;
    }

    public void setForce(double force) {
        this.force = force;
    }

    public boolean isBlockBreaking() {
        return this.blockBreaking;
    }


    public void setBlockBreaking(boolean affectsBlocks) {
        this.blockBreaking = affectsBlocks;
    }
}
