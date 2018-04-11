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

package cn.nukkit.api.event.inventory;

import cn.nukkit.api.event.Cancellable;
import cn.nukkit.api.inventory.Inventory;
import cn.nukkit.api.item.ItemInstance;

public class InventoryClickEvent implements InventoryEvent, Cancellable {
    private final Inventory inventory;
    private final int slot;
    private final ItemInstance sourceItem;
    private final ItemInstance heldItem;
    private final ItemInstance leftItem;
    private boolean cancelled;

    public InventoryClickEvent(Inventory inventory, int slot, ItemInstance sourceItem, ItemInstance heldItem, ItemInstance leftItem) {
        this.inventory = inventory;
        this.slot = slot;
        this.sourceItem = sourceItem;
        this.heldItem = heldItem;
        this.leftItem = leftItem;
    }

    public int getSlot() {
        return slot;
    }

    public ItemInstance getSourceItem() {
        return sourceItem;
    }

    public ItemInstance getHeldItem() {
        return heldItem;
    }

    public ItemInstance getLeftItem() {
        return leftItem;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}