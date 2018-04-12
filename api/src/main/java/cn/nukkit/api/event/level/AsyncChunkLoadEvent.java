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

package cn.nukkit.api.event.level;

import cn.nukkit.api.level.Level;
import cn.nukkit.api.level.chunk.Chunk;

public class AsyncChunkLoadEvent implements ChunkEvent {
    private final Chunk chunk;
    private final boolean newChunk;

    public AsyncChunkLoadEvent(Chunk chunk, boolean newChunk) {
        this.chunk = chunk;
        this.newChunk = newChunk;
    }

    public boolean isNewChunk() {
        return newChunk;
    }

    @Override
    public Chunk getChunk() {
        return null;
    }

    @Override
    public Level getLevel() {
        return null;
    }
}