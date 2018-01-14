package cn.nukkit.api.entity.component;

import cn.nukkit.api.event.entity.EntityDamageEvent;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public interface Damageable extends EntityComponent {

    int getRoundedHealth();

    /**
     * Gets the entity's health from 0 to max health, where 0 is dead.
     *
     * @return Health represented from 0 to max
     */
    float getHealth();

    /**
     * Sets the entity's health from 0 to max health, where 0 is
     * dead.
     *
     * @param health New health represented from 0 to max
     */
    void setHealth(@Nonnegative float health);

    @Nonnegative
    int getMaximumHealth();

    void setMaximumHealth(@Nonnegative int maximumHealth);

    /**
     * Deals the given amount of damage to this entity.
     *
     * @param damage Amount of damage to deal
     */
    void damage(@Nonnegative float damage);

    /**
     * Deals the given amount of damage to this entity, from a specified
     * entity.
     *
     * @param damage Amount of damage to deal
     * @param source Entity which to attribute this damage from
     */
    void damage(@Nonnegative float damage, @Nullable EntityDamageEvent source);

    default boolean isDead() {
        return getHealth() < 1;
    }

    /**
     * Retrieve the last {@link EntityDamageEvent} inflicted on this entity.
     * This event may have been cancelled.
     *
     * @return the last known {@link EntityDamageEvent} or null if hitherto
     * unharmed
     */
    @Nonnull
    Optional<EntityDamageEvent> getLastDamageCause();

    /**
     * Record the last {@link EntityDamageEvent} inflicted on this entity
     *
     * @param event a {@link EntityDamageEvent}
     */
    void setLastDamageCause(@Nullable EntityDamageEvent event);
}
