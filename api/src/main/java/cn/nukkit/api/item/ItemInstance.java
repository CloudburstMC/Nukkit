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
import javax.annotation.concurrent.Immutable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Nonnull
@Immutable
public interface ItemInstance {

    ItemType getItemType();

    int getAmount();

    Optional<Metadata> getItemData();

    Optional<String> getName();

    List<String> getLore();

    Collection<EnchantmentInstance> getEnchantments();

    ItemInstanceBuilder toBuilder();

    boolean isSimilar(@Nonnull ItemInstance itemInstance);

    boolean isMergeable(@Nonnull ItemInstance itemInstance);

    default boolean isFull() {
        return getAmount() >= getItemType().getMaximumStackSize();
    }
}
