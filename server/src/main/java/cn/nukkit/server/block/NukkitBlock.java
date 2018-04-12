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

package cn.nukkit.server.block;

import cn.nukkit.api.block.Block;
import cn.nukkit.api.block.BlockState;
import cn.nukkit.api.level.Level;
import cn.nukkit.api.level.chunk.Chunk;
import cn.nukkit.api.util.BoundingBox;
import com.flowpowered.math.vector.Vector3i;

public class NukkitBlock implements Block {
    private final Level level;
    private final Chunk chunk;
    private final Vector3i blockPosition;
    private final BlockState state;

    public NukkitBlock(Level level, Chunk chunk, Vector3i blockPosition, BlockState state) {
        this.level = level;
        this.chunk = chunk;
        this.blockPosition = blockPosition;
        this.state = state;
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public Chunk getChunk() {
        return chunk;
    }

    @Override
    public Vector3i getBlockPosition() {
        return blockPosition;
    }

    @Override
    public BoundingBox getBoundingBox() {
        return new BoundingBox(blockPosition.toFloat().floor(), blockPosition.toFloat().ceil());
    }

    @Override
    public BlockState getBlockState() {
        return state;
    }
}
