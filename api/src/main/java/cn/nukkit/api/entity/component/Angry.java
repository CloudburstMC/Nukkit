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

public interface Angry extends EntityComponent {

    /**
     * Checks if this entity is angry
     *
     * @return Anger true if angry
     */
    boolean isAngry();

    /**
     * Sets the anger of this entity.
     * <p>
     * An angry entity can not be fed or tamed, and will actively look for
     * targets to attack.
     *
     * @param angry true if angry
     */
    void setAngry(boolean angry);

    boolean canAttack(@Nonnull Entity entity);
}
