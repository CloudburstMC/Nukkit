package cn.nukkit.api.entity.item;

import cn.nukkit.api.entity.Entity;

/**
 * @author CreeperFace
 */
public interface PrimedTNT extends Entity {

    /**
     * Retrieve the number of ticks until the explosion of this TNTPrimed
     * entity
     *
     * @return the number of ticks until this TNTPrimed explodes
     */
    int getFuseTicks();

    /**
     * Set the number of ticks until the TNT blows up after being primed.
     *
     * @param fuseTicks The fuse ticks
     */
    void setFuseTicks(int fuseTicks);
}
