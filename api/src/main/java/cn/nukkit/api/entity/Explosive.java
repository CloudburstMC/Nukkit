package cn.nukkit.api.entity;

/**
 * @author CreeperFace
 */
public interface Explosive extends Entity {

    /**
     * Set the radius affected by this explosive's explosion
     *
     * @param radius The explosive radius
     */
    void setRadius(double radius);

    /**
     * Return the radius of this explosive's explosion
     *
     * @return the radius of blocks affected
     */
    double getRadius();

    /**
     * Set whether or not this explosive's explosion causes fire
     *
     * @param isIncendiary Whether it should cause fire
     */
    void setIsIncendiary(boolean isIncendiary);

    /**
     * Return whether or not this explosive creates a fire when exploding
     *
     * @return true if the explosive creates fire, false otherwise
     */
    boolean isIncendiary();
}
