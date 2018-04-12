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

public interface PickupDelay extends EntityComponent {

    default boolean canPickup() {
        return getDelayPickupTicks() <= 0;
    }

    /**
     * Gets the period of time (in ticks) before this item stack can be picked up.
     *
     * @return item stack delay
     */
    @Nonnegative
    int getDelayPickupTicks();

    /**
     * Sets the period of time (in ticks) before this item stack can be picked up.
     *
     * @param ticks item stack delay ticks
     */
    void setDelayPickupTicks(@Nonnegative int ticks);
}
