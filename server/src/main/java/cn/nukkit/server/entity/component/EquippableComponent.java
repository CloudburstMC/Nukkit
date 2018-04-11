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

package cn.nukkit.server.entity.component;

import cn.nukkit.api.entity.component.Equippable;
import cn.nukkit.api.item.ItemInstance;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class EquippableComponent implements Equippable {
    private ItemInstance helmet;
    private ItemInstance chestplate;
    private ItemInstance leggings;
    private ItemInstance boots;

    @Nonnull
    @Override
    public Optional<ItemInstance> getHelmet() {
        return Optional.ofNullable(helmet);
    }

    @Override
    public void setHelmet(@Nullable ItemInstance helmet) {
        this.helmet = helmet;
    }

    @Nonnull
    @Override
    public Optional<ItemInstance> getChestplate() {
        return Optional.ofNullable(chestplate);
    }

    @Override
    public void setChestplate(@Nullable ItemInstance chestplate) {
        this.chestplate = chestplate;
    }

    @Nonnull
    @Override
    public Optional<ItemInstance> getLeggings() {
        return Optional.ofNullable(leggings);
    }

    @Override
    public void setLeggings(@Nullable ItemInstance leggings) {
        this.leggings = leggings;
    }

    @Nonnull
    @Override
    public Optional<ItemInstance> getBoots() {
        return Optional.ofNullable(boots);
    }

    @Override
    public void setBoots(@Nullable ItemInstance boots) {
        this.boots = boots;
    }
}
