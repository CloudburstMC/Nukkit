package cn.nukkit.api.entity.component;

import javax.annotation.Nonnegative;

/**
 * This represent the amount of time till the entity's fire is extinguished.
 * If an entity has this component, it is on fire.
 */
public interface Flammable extends EntityComponent {

    default boolean isIgnited() {
        return getIgnitedDuration() <= 0;
    }

    /**
     * Gets the duration (in ticks) before the entity is extinguished.
     *
     * @return ticks
     */
    @Nonnegative
    int getIgnitedDuration();

    /**
     * Sets the duration (in ticks) before the entity is extinguished.
     *
     * @param ticks ticks
     */
    void setIgnitedDuration(@Nonnegative int ticks);
}
