package cn.nukkit.api.entity;

/**
 * @author CreeperFace
 */
public interface Damageable extends Entity {

    /**
     * Deals the given amount of damage to this entity.
     *
     * @param amount Amount of damage to deal
     */
    void damage(double amount);

    /**
     * Deals the given amount of damage to this entity, from a specified
     * entity.
     *
     * @param amount Amount of damage to deal
     * @param source Entity which to attribute this damage from
     */
    void damage(double amount, Entity source);

    /**
     * Gets the entity's health from 0 to max health, where 0 is dead.
     *
     * @return Health represented from 0 to max
     */
    double getHealth();

    /**
     * Sets the entity's health from 0 to max health, where 0 is
     * dead.
     *
     * @param health New health represented from 0 to max
     */
    void setHealth(double health);
}
