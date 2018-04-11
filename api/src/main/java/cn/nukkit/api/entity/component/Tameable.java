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

import cn.nukkit.api.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public interface Tameable extends EntityComponent {

    /**
     * Check if this is tamed
     * <p>
     * If something is tamed then a player can not tame it through normal
     * methods, even if it does not belong to anyone in particular.
     *
     * @return true if this has been tamed
     */
    boolean isTamed();

    /**
     * Sets if this has been tamed. Not necessary if the method setOwner has
     * been used, as it tames automatically.
     * <p>
     * If something is tamed then a player can not tame it through normal
     * methods, even if it does not belong to anyone in particular.
     *
     * @param tame true if tame
     */
    void setTamed(boolean tame);

    /**
     * Gets the current owning AnimalTamer
     *
     * @return the owning AnimalTamer, or null if not owned
     */
    @Nonnull
    Optional<Player> getOwner();

    /**
     * Set this to be owned by given AnimalTamer.
     * <p>
     * If the owner is not null, this will be tamed and will have any current
     * path it is following removed. If the owner is set to null, this will be
     * untamed, and the current owner removed.
     *
     * @param tamer the AnimalTamer who should own this
     */
    void setOwner(@Nullable Player tamer);
}
