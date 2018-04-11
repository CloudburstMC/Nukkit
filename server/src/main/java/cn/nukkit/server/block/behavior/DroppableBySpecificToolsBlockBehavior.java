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

package cn.nukkit.server.block.behavior;

import cn.nukkit.api.block.Block;
import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.api.item.ItemType;
import cn.nukkit.api.item.ItemTypes;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;
import java.util.List;

import static cn.nukkit.api.item.ItemTypes.*;

public class DroppableBySpecificToolsBlockBehavior extends SimpleBlockBehavior {
    public static final BlockBehavior ALL_PICKAXES = new DroppableBySpecificToolsBlockBehavior(
            ImmutableList.of(WOODEN_PICKAXE, STONE_PICKAXE, IRON_PICKAXE, GOLDEN_PICKAXE, DIAMOND_PICKAXE));
    public static final BlockBehavior STONE_PICKAXE_AND_ABOVE = new DroppableBySpecificToolsBlockBehavior(
            ImmutableList.of(STONE_PICKAXE, IRON_PICKAXE, GOLDEN_PICKAXE, DIAMOND_PICKAXE));
    public static final BlockBehavior IRON_PICKAXE_AND_ABOVE = new DroppableBySpecificToolsBlockBehavior(
            ImmutableList.of(IRON_PICKAXE, GOLDEN_PICKAXE, DIAMOND_PICKAXE));
    public static final BlockBehavior GOLDEN_PICKAXE_AND_ABOVE = new DroppableBySpecificToolsBlockBehavior(
            ImmutableList.of(GOLDEN_PICKAXE, DIAMOND_PICKAXE));
    public static final BlockBehavior DIAMOND_PICKAXE_AND_ABOVE = new DroppableBySpecificToolsBlockBehavior(
            ImmutableList.of(DIAMOND_PICKAXE));
    public static final BlockBehavior SHEARS = new DroppableBySpecificToolsBlockBehavior(ImmutableList.of(ItemTypes.SHEARS));


    private final List<ItemType> itemsAllowed;

    public DroppableBySpecificToolsBlockBehavior(List<ItemType> itemsAllowed) {
        Preconditions.checkNotNull(itemsAllowed, "itemsAllowed");
        this.itemsAllowed = itemsAllowed;
    }

    @Override
    public boolean isToolCompatible(Block block, @Nullable ItemInstance item) {
        return item != null && itemsAllowed.contains(item.getItemType());
    }
}
