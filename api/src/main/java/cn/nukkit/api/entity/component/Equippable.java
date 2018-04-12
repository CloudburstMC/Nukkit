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

import cn.nukkit.api.item.ItemInstance;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNullableByDefault;
import java.util.Optional;

@Nonnull
@ParametersAreNullableByDefault
public interface Equippable extends EntityComponent {
    /**
     * Gets the helmet the entity is wearing.
     *
     * @return the helmet worn
     */
    Optional<ItemInstance> getHelmet();

    /**
     * Sets the helmet the entity is wearing.
     *
     * @param helmet the helmet to wear
     */
    void setHelmet(ItemInstance helmet);

    /**
     * Gets the chestplate the entity is wearing.
     *
     * @return the chestplate worn
     */
    Optional<ItemInstance> getChestplate();

    /**
     * Sets the chestplate the entity is wearing.
     *
     * @param chestplate the chestplate to wear
     */
    void setChestplate(ItemInstance chestplate);

    /**
     * Gets the leggings the entity is wearing.
     *
     * @return the leggings worn
     */
    Optional<ItemInstance> getLeggings();

    /**
     * Sets the leggings the entity is wearing.
     *
     * @param leggings the leggings to wear
     */
    void setLeggings(ItemInstance leggings);

    /**
     * Gets the boots the entity is wearing.
     *
     * @return the boots worn
     */
    Optional<ItemInstance> getBoots();

    /**
     * Sets the boots the entity is wearing.
     *
     * @param boots the boots to wear
     */
    void setBoots(ItemInstance boots);
}
