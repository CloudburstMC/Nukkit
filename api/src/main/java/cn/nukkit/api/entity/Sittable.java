package cn.nukkit.api.entity;

/**
 * @author CreeperFace
 */
public interface Sittable extends Entity {

    /**
     * Sets if this animal is sitting. Will remove any path that the animal
     * was following beforehand.
     *
     * @param sitting true if sitting
     */
    void setSitting(boolean sitting);

    /**
     * Checks if this animal is sitting
     *
     * @return true if sitting
     */
    boolean isSitting();
}
