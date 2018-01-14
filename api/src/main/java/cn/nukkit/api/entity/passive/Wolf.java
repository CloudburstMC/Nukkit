package cn.nukkit.api.entity.passive;

import cn.nukkit.api.entity.Entity;
import cn.nukkit.api.util.data.DyeColor;

/**
 * @author CreeperFace
 */
public interface Wolf extends Sittable, Entity {

    /**
     * Checks if this wolf is angry
     *
     * @return Anger true if angry
     */
    boolean isAngry();

    /**
     * Sets the anger of this wolf.
     * <p>
     * An angry wolf can not be fed or tamed, and will actively look for
     * targets to attack.
     *
     * @param angry true if angry
     */
    void setAngry(boolean angry);

    /**
     * Get the collar color of this wolf
     *
     * @return the color of the collar
     */
    DyeColor getCollarColor();

    /**
     * Set the collar color of this wolf
     *
     * @param color the color to apply
     */
    void setCollarColor(DyeColor color);

}
