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

package cn.nukkit.server.inventory;

import cn.nukkit.api.inventory.Inventory;
import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.server.network.minecraft.session.PlayerSession;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * An object that can view a specific inventory.
 */
@ParametersAreNonnullByDefault
public interface InventoryObserver {

    /**
     * Called when a single slot is changed within the observed inventory.
     *
     * @param slot      slot in question
     * @param oldItem   old item in slot
     * @param newItem   new item in slot
     * @param inventory inventory being observed
     * @param session   session which caused the change
     */
    void onInventorySlotChange(int slot, @Nullable ItemInstance oldItem, @Nullable ItemInstance newItem, Inventory inventory, @Nullable PlayerSession session);

    /**
     * Called when the entire contents of the inventory is changed within the observed inventory.
     *
     * @param contents  new contents of the inventory
     * @param inventory inventory being observed
     */
    void onInventoryContentsChange(ItemInstance[] contents, Inventory inventory);
}
