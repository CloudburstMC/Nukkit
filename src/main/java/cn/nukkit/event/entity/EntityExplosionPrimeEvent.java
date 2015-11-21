package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;

/**
 * Created on 15-10-27.
 */
public class EntityExplosionPrimeEvent extends EntityEvent implements Cancellable {

    private float force;
    private boolean blockBreaking;

    public EntityExplosionPrimeEvent(Entity entity, float force) {
        this.entity = entity;
        this.force = force;
    }

    public float getForce() {
        return force;
    }

    public void setForce(float force) {
        this.force = force;
    }

    public boolean isBlockBreaking() {
        return blockBreaking;
    }

    public void setBlockBreaking(boolean blockBreaking) {
        this.blockBreaking = blockBreaking;
    }

}
