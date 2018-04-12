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

package cn.nukkit.server.level.generator;

import cn.nukkit.api.block.BlockState;
import cn.nukkit.api.block.BlockTypes;
import cn.nukkit.api.level.Level;
import cn.nukkit.api.level.chunk.Chunk;
import cn.nukkit.api.level.chunk.generator.ChunkGenerator;
import cn.nukkit.server.block.NukkitBlockState;
import com.flowpowered.math.vector.Vector3f;

import java.util.Random;

public class FlatChunkGenerator implements ChunkGenerator {
    private static final BlockState BEDROCK = new NukkitBlockState(BlockTypes.BEDROCK, null, null);
    private static final BlockState DIRT = new NukkitBlockState(BlockTypes.DIRT, null, null);
    private static final BlockState GRASS = new NukkitBlockState(BlockTypes.GRASS_BLOCK, null, null);

    private static final Vector3f SPAWN = new Vector3f(0, 7, 0);

    @Override
    public void generateChunk(Level level, Chunk chunk, Random random) {
        for (int x1 = 0; x1 < 16; x1++) {
            for (int z1 = 0; z1 < 16; z1++) {
                chunk.setBlock(x1, 0, z1, BEDROCK, false);
                for (int y = 1; y < 4; y++) {
                    chunk.setBlock(x1, y, z1, DIRT, false);
                }
                chunk.setBlock(x1, 4, z1, GRASS, false);
            }
        }
    }

    @Override
    public void populateChunk(Level level, Chunk chunk, Random random) {

    }

    @Override
    public Vector3f getDefaultSpawn() {
        return SPAWN;
    }
}
