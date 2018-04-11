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

import cn.nukkit.api.block.BlockTypes;
import cn.nukkit.api.entity.component.ContainedItem;
import cn.nukkit.api.item.ItemInstance;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;

public class ContainedItemComponent implements ContainedItem {
    private ItemInstance item;
    private volatile boolean stale = false;

    public ContainedItemComponent(@Nonnull ItemInstance item) {
        Preconditions.checkNotNull(item, "item");
        Preconditions.checkArgument(item.getItemType() != BlockTypes.AIR);
        this.item = item;
    }

    @Override
    public ItemInstance getItem() {
        return item;
    }

    @Override
    public void setItem(@Nonnull ItemInstance item) {
        Preconditions.checkNotNull(item);
        if (!this.item.equals(item)) {
            this.item = item;
            stale = true;
        }
    }

    public boolean isStale() {
        return stale;
    }

    public void refresh() {
        stale = false;
    }
}
