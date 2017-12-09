package cn.nukkit.api.entity;

/**
 * @author CreeperFace
 */
public interface Creature extends LivingEntity {

    /**
     * Instructs this Creature to set the specified LivingEntity as its
     * target.
     * <p>
     * Hostile creatures may attack their target, and friendly creatures may
     * follow their target.
     *
     * @param target New LivingEntity to target, or null to clear the target
     */
    void setTarget(LivingEntity target);

    /**
     * Gets the current target of this Creature
     *
     * @return Current target of this creature, or null if none exists
     */
    LivingEntity getTarget();
}
