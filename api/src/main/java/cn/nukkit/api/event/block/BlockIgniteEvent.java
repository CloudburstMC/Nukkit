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

import cn.nukkit.api.block.Block;
import cn.nukkit.api.entity.Entity;
import cn.nukkit.api.event.Cancellable;

public class BlockIgniteEvent implements BlockEvent, Cancellable {
    private final Block block;
    private final Block source;
    private final Entity entity;
    private final BlockIgniteCause cause;
    private boolean cancelled;

    public BlockIgniteEvent(Block block, Block source, Entity entity, BlockIgniteCause cause) {
        this.block = block;
        this.source = source;
        this.entity = entity;
        this.cause = cause;
    }

    public Block getSource() {
        return source;
    }

    public Entity getEntity() {
        return entity;
    }

    public BlockIgniteCause getCause() {
        return cause;
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

    public enum BlockIgniteCause {
        EXPLOSION,
        FIREBALL,
        FLINT_AND_STEEL,
        LAVA,
        LIGHTNING,
        SPREAD
    }
}
