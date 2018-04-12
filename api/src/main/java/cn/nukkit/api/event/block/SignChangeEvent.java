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
import com.google.common.base.Preconditions;

public class SignChangeEvent implements BlockEvent, Cancellable {
    private final Block block;
    private final Player player;
    private String[] lines;
    private boolean cancelled;

    public SignChangeEvent(Block block, Player player, String[] lines) {
        this.block = block;
        this.player = player;
        this.lines = lines;
    }

    public Player getPlayer() {
        return player;
    }

    public String[] getLines() {
        return lines;
    }

    public String getLine(int index) {
        Preconditions.checkArgument(index > 0 && index < 4, "index must be from 0 to 3");
        return this.lines[index];
    }

    public void setLine(int index, String line) {
        Preconditions.checkArgument(index > 0 && index < 4, "index must be from 0 to 3");
        this.lines[index] = line;
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
