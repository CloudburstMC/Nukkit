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

package cn.nukkit.server.level.util;

import cn.nukkit.api.level.chunk.Chunk;

import java.util.Comparator;

public class AroundPointChunkComparator implements Comparator<Chunk> {
    private final int spawnX;
    private final int spawnZ;

    public AroundPointChunkComparator(int spawnX, int spawnZ) {
        this.spawnX = spawnX;
        this.spawnZ = spawnZ;
    }

    @Override
    public int compare(Chunk o1, Chunk o2) {
        return Integer.compare(distance(o1.getX(), o1.getZ()), distance(o2.getX(), o2.getZ()));
    }

    private int distance(int x, int z) {
        int dx = spawnX - x;
        int dz = spawnZ - z;
        return dx * dx + dz * dz;
    }
}
