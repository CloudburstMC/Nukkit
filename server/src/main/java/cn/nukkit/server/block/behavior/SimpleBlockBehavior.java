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

import cn.nukkit.api.Player;
import cn.nukkit.api.block.Block;
import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.api.item.ItemInstanceBuilder;
import cn.nukkit.server.item.behavior.ItemBehaviorUtil;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;
import java.util.Collection;

public class SimpleBlockBehavior implements BlockBehavior {
    public static final SimpleBlockBehavior INSTANCE = new SimpleBlockBehavior();

    @Override
    public Collection<ItemInstance> getDrops(Player player, Block block, @Nullable ItemInstance item) {
        if (!isToolCompatible(block, item)) {
            return ImmutableList.of();
        }
        // TODO: Fortune drops
        ItemInstanceBuilder builder = player.getServer().itemInstanceBuilder()
                .itemType(block.getBlockState().getBlockType())
                .amount(1);
        if (block.getBlockState().getBlockData() != null) {
            builder.itemData(block.getBlockState().getBlockData());
        }

        return ImmutableList.of(builder.build());
    }

    @Override
    public float getBreakTime(Player player, Block block, @Nullable ItemInstance item) {
        float breakTime = block.getBlockState().getBlockType().hardness();

        if (isToolCompatible(block, item)) {
            breakTime *= 1.5;
            breakTime /= ItemBehaviorUtil.getMiningEfficiency(item);
        } else {
            breakTime *= 5;
            breakTime /= 1;
        }

        return breakTime;
    }

    @Override
    public boolean isToolCompatible(Block block, @Nullable ItemInstance item) {
        return block.getBlockState().getBlockType().isDiggable();
    }
}
