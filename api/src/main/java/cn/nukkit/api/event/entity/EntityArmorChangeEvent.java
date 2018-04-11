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

package cn.nukkit.api.event.entity;

import cn.nukkit.api.entity.Entity;
import cn.nukkit.api.event.Cancellable;
import cn.nukkit.api.item.ItemInstance;

public class EntityArmorChangeEvent implements EntityEvent, Cancellable {
    private final Entity entity;
    private final ItemInstance oldItem;
    private final int slot;
    private ItemInstance newItem;
    private boolean cancelled;

    public EntityArmorChangeEvent(Entity entity, ItemInstance oldItem, ItemInstance newItem, int slot) {
        this.entity = entity;
        this.oldItem = oldItem;
        this.newItem = newItem;
        this.slot = slot;
    }

    public int getSlot() {
        return slot;
    }

    public ItemInstance getNewItem() {
        return newItem;
    }

    public void setNewItem(ItemInstance newItem) {
        this.newItem = newItem;
    }

    public ItemInstance getOldItem() {
        return oldItem;
    }

    @Override
    public Entity getEntity() {
        return entity;
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
