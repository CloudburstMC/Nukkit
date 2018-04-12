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

public interface Explode extends EntityComponent {

    boolean isPrimed();

    /**
     * Return the radius of this explosive's explosion
     *
     * @return the radius of blocks affected
     */
    @Nonnegative
    int getRadius();

    /**
     * Set the radius affected by this explosive's explosion
     *
     * @param radius The explosive radius
     */
    void setRadius(@Nonnegative int radius);

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
