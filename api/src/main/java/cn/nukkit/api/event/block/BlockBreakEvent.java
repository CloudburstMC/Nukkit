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
import cn.nukkit.api.event.Cancellable;
import cn.nukkit.api.item.ItemInstance;

import java.util.ArrayList;
import java.util.Collection;

public class BlockBreakEvent implements BlockEvent, Cancellable {

    protected final ItemInstance item;
    protected final Player player;
    private final Block block;
    protected boolean instaBreak;
    protected Collection<ItemInstance> drops;
    private boolean fastBreak;
    private boolean cancelled;

    public BlockBreakEvent(Player player, Block block, Collection<ItemInstance> drops, ItemInstance item, boolean instaBreak, boolean fastBreak) {
        this.block = block;
        this.item = item;
        this.player = player;
        this.instaBreak = instaBreak;
        this.drops = new ArrayList<>(drops);
        this.fastBreak = fastBreak;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemInstance getItem() {
        return item;
    }

    public boolean getInstaBreak() {
        return this.instaBreak;
    }

    public void setInstaBreak(boolean instaBreak) {
        this.instaBreak = instaBreak;
    }

    public Collection<ItemInstance> getDrops() {
        return drops;
    }

    public boolean isFastBreak() {
        return this.fastBreak;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public Block getBlock() {
        return block;
    }
}
