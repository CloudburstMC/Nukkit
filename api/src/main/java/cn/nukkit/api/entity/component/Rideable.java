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

import cn.nukkit.api.entity.Entity;

import javax.annotation.Nonnull;
import java.util.List;

public interface Rideable extends EntityComponent {
    /**
     * Gets a list of passengers of this vehicle.
     * <p>
     * The returned list will not be directly linked to the entity's current
     * passengers, and no guarantees are made as to its mutability.
     *
     * @return list of entities corresponding to current passengers.
     */
    List<Entity> getPassengers();

    /**
     * Add a passenger to the vehicle.
     *
     * @param passenger The passenger to add
     * @return false if it could not be done for whatever reason
     */
    boolean addPassenger(@Nonnull Entity passenger);

    /**
     * Remove a passenger from the vehicle.
     *
     * @param passenger The passenger to remove
     * @return false if it could not be done for whatever reason
     */
    boolean removePassenger(@Nonnull Entity passenger);

    /**
     * Check if a vehicle has passengers.
     *
     * @return True if the vehicle has no passengers.
     */
    boolean isEmpty();

    /**
     * Eject any passenger.
     *
     * @return True if there was a passenger.
     */
    boolean eject();
}
