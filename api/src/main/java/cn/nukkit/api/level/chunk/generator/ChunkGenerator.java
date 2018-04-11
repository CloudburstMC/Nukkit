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

package cn.nukkit.api.level.chunk.generator;

import cn.nukkit.api.level.Level;
import cn.nukkit.api.level.chunk.Chunk;
import cn.nukkit.api.level.data.Biome;
import cn.nukkit.api.level.data.Dimension;
import cn.nukkit.api.level.data.Generator;
import com.flowpowered.math.vector.Vector3f;

import java.util.Random;

public interface ChunkGenerator {

    void generateChunk(Level level, Chunk chunk, Random random);

    void populateChunk(Level level, Chunk chunk, Random random);

    Vector3f getDefaultSpawn();

    /**
     * The dimension sent to the client which will result in a different loading screen background.
     *
     * @return dimension
     */
    default Dimension getDimension() {
        return Dimension.OVERWORLD;
    }

    /**
     * Generator sent to the client.
     *
     * @return
     */
    default Generator getGenerator() {
        return Generator.UNDEFINED;
    }
}
