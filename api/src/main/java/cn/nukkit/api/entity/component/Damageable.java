/*
 * GNU GENERAL PUBLIC LICENSE
 * Copyright (C) 2018 NukkitX Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * Contact info: info@nukkitx.com
 */

package cn.nukkit.api.entity.component;

import javax.annotation.Nonnegative;

public interface Damageable extends EntityComponent {

    @Nonnegative
    float getFixedHealth();

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

    void replenish(@Nonnegative float replenish);

    /*/**
     * Deals the given amount of damage to this entity, from a specified
     * entity.
     *
     * @param damage Amount of damage to deal
     * @param source Entity which to attribute this damage from
     */
    //void damage(@Nonnegative float damage, @Nullable EntityDamageEvent source);

    default boolean isDead() {
        return getHealth() < 1;
    }

    /*/**
     * Retrieve the last {@link EntityDamageEvent} inflicted on this entity.
     * This event may have been cancelled.
     *
     * @return the last known {@link EntityDamageEvent} or null if hitherto
     * unharmed
     *//*
    @Nonnull
    Optional<EntityDamageEvent> getLastDamageCause();

    /**
     * ItemRecord the last {@link EntityDamageEvent} inflicted on this entity
     *
     * @param event a {@link EntityDamageEvent}
     */
    //void setLastDamageCause(@Nullable EntityDamageEvent event);
}
