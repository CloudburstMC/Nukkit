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

package cn.nukkit.api.item;

import cn.nukkit.api.enchantment.EnchantmentInstance;
import cn.nukkit.api.metadata.Metadata;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

public interface ItemInstanceBuilder {

    ItemInstanceBuilder itemType(@Nonnull ItemType itemType);

    ItemInstanceBuilder amount(int amount);

    ItemInstanceBuilder name(@Nonnull String name);

    ItemInstanceBuilder clearName();

    ItemInstanceBuilder lore(List<String> lines);

    ItemInstanceBuilder clearLore();

    ItemInstanceBuilder itemData(Metadata data);

    ItemInstanceBuilder addEnchantment(EnchantmentInstance enchantment);

    ItemInstanceBuilder addEnchantments(Collection<EnchantmentInstance> enchantmentInstanceCollection);

    ItemInstanceBuilder clearEnchantments();

    ItemInstanceBuilder removeEnchantment(EnchantmentInstance enchantment);

    ItemInstanceBuilder removeEnchantments(Collection<EnchantmentInstance> enchantments);

    ItemInstance build();
}
