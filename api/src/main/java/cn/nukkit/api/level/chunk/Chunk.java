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

package cn.nukkit.api.level.chunk;

import cn.nukkit.api.block.Block;
import cn.nukkit.api.block.BlockState;
import cn.nukkit.api.level.Level;
import cn.nukkit.api.level.data.Biome;

public interface Chunk {
    int getX();

    int getZ();

    Level getLevel();

    void setBiome(int x, int z, Biome biome);

    Block getBlock(int x, int y, int z);

    Block setBlock(int x, int y, int z, BlockState state);

    Block setBlock(int x, int y, int z, BlockState state, boolean shouldRecalculateLight);

    int getHighestLayer(int x, int z);

    byte getSkyLight(int x, int y, int z);

    byte getBlockLight(int x, int y, int z);

    ChunkSnapshot toSnapshot();
}
