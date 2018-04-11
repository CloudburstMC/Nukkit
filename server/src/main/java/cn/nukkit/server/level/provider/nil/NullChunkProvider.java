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

package cn.nukkit.server.level.provider.nil;

import cn.nukkit.api.level.Level;
import cn.nukkit.api.level.chunk.Chunk;
import cn.nukkit.server.level.provider.ChunkProvider;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class NullChunkProvider implements ChunkProvider {
    @Override
    public CompletableFuture<Chunk> createChunk(Level level, int x, int z, Executor executor) {
        // Do nothing
        CompletableFuture<Chunk> chunkFuture = new CompletableFuture<>();
        chunkFuture.complete(null);
        return chunkFuture;
    }

    @Override
    public void saveChunk(Chunk chunk, Executor executor) {
        // Do nothing
    }
}
