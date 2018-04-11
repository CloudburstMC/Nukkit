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

import cn.nukkit.api.block.BlockState;
import cn.nukkit.api.block.BlockStateBuilder;
import cn.nukkit.api.block.BlockType;
import cn.nukkit.api.block.entity.BlockEntity;
import cn.nukkit.api.metadata.Metadata;
import com.google.common.base.Preconditions;

import java.util.Optional;

public class NukkitBlockState implements BlockState {
    private final BlockType type;
    private final Metadata metadata;
    private final BlockEntity blockEntity;

    public NukkitBlockState(BlockType type, Metadata metadata, BlockEntity blockEntity) {
        this.type = Preconditions.checkNotNull(type, "type");
        this.metadata = metadata;
        this.blockEntity = blockEntity;
    }

    @Override
    public BlockType getBlockType() {
        return type;
    }

    @Override
    public Metadata getBlockData() {
        return metadata;
    }

    @Override
    public Optional<BlockEntity> getBlockEntity() {
        return Optional.ofNullable(blockEntity);
    }

    @Override
    public BlockStateBuilder toBuilder() {
        return new NukkitBlockStateBuilder().setBlockType(type).setMetadata(blockEntity == null ? metadata : blockEntity);
    }
}
