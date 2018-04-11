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
import com.flowpowered.math.vector.Vector3f;

public class PlayerInteractEvent implements PlayerEvent, Cancellable {
    private final Player player;
    private final Block blockTouched;
    private final Vector3f touchVector;
    private final BlockFace blockFace;
    private final ItemInstance item;
    private final Action action;
    private boolean cancelled;

    public PlayerInteractEvent(Player player, ItemInstance item, Block block, Vector3f touch, BlockFace face) {
        this(player, item, block, touch, face, Action.RIGHT_CLICK_BLOCK);
    }

    public PlayerInteractEvent(Player player, ItemInstance item, Block block, Vector3f touch, BlockFace face, Action action) {
        this.player = player;
        this.blockTouched = block;
        this.touchVector = touch;
        this.item = item;
        this.blockFace = face;
        this.action = action;
    }

    public enum Action {
        LEFT_CLICK_BLOCK,
        RIGHT_CLICK_BLOCK,
        LEFT_CLICK_AIR,
        RIGHT_CLICK_AIR,
        PHYSICAL
    }

    public ItemInstance getItem() {
        return item;
    }

    public BlockFace getBlockFace() {
        return blockFace;
    }

    public Action getAction() {
        return action;
    }

    public Block getBlockTouched() {
        return blockTouched;
    }

    public Vector3f getTouchVector() {
        return touchVector;
    }

    @Override
    public Player getPlayer() {
        return player;
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
