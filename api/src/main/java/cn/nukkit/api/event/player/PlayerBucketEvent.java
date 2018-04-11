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

package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.block.Block;
import cn.nukkit.api.event.Cancellable;
import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.api.util.data.BlockFace;

abstract class PlayerBucketEvent implements PlayerEvent, Cancellable {
    private final Player player;
    private final Block blockClicked;
    private final BlockFace blockFace;
    private final ItemInstance bucket;
    private boolean cancelled;
    private ItemInstance item;


    public PlayerBucketEvent(Player who, Block blockClicked, BlockFace blockFace, ItemInstance bucket, ItemInstance itemInHand) {
        this.player = who;
        this.blockClicked = blockClicked;
        this.blockFace = blockFace;
        this.item = itemInHand;
        this.bucket = bucket;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    public Block getBlockClicked() {
        return blockClicked;
    }

    public BlockFace getBlockFace() {
        return blockFace;
    }

    public ItemInstance getBucket() {
        return bucket;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public ItemInstance getItem() {
        return item;
    }

    public void setItem(ItemInstance item) {
        this.item = item;
    }
}
