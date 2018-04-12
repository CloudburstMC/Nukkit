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

package cn.nukkit.api.event.block;

import cn.nukkit.api.Player;
import cn.nukkit.api.block.Block;
import cn.nukkit.api.block.entity.ItemFrameBlockEntity;
import cn.nukkit.api.event.Cancellable;
import cn.nukkit.api.item.ItemInstance;

public class ItemFrameDropItemEvent implements BlockEvent, Cancellable {
    private final Block block;
    private final Player player;
    private final ItemInstance item;
    private final ItemFrameBlockEntity itemFrame;
    private boolean cancellable;

    public ItemFrameDropItemEvent(Player player, Block block, ItemFrameBlockEntity itemFrame, ItemInstance item) {
        this.block = block;
        this.player = player;
        this.item = item;
        this.itemFrame = itemFrame;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemFrameBlockEntity getItemFrame() {
        return itemFrame;
    }

    public ItemInstance getItem() {
        return item;
    }

    @Override
    public boolean isCancelled() {
        return cancellable;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancellable = cancelled;
    }

    @Override
    public Block getBlock() {
        return block;
    }
}