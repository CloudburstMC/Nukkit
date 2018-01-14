package cn.nukkit.api.entity.weather;

import cn.nukkit.api.entity.Entity;

/**
 * @author CreeperFace
 */
public interface Lightning extends Entity {

    /**
     * Returns whether the strike is an effect that does no damage.
     *
     * @return whether the strike is an effect
     */
    boolean isEffect();
}
