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

package cn.nukkit.server.metadata.serializer;

import cn.nukkit.api.block.BlockState;
import cn.nukkit.api.block.entity.BlockEntity;
import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.api.item.ItemType;
import cn.nukkit.api.metadata.Metadata;
import cn.nukkit.server.nbt.tag.CompoundTag;

import java.util.Optional;

@SuppressWarnings("unchecked")
public interface Serializer {
    CompoundTag readNBT(BlockState block);

    short readMetadata(BlockState block);

    CompoundTag readNBT(ItemInstance item);

    short readMetadata(ItemInstance item);

    Metadata writeMetadata(ItemType type, short metadata);

    BlockEntity writeNBT(ItemType type, CompoundTag nbtTag);

    default <T> T getItemDataOrNull(ItemInstance item) {
        return (T) item.getItemData().orElse(null);
    }

    default <T> Optional<T> getItemData(ItemInstance item) {
        return (Optional<T>) item.getItemData();
    }

    default <T> T getBlockData(BlockState block) {
        return (T) block.getBlockData();
    }

    default <T> T getBlockStateEntityOrNull(BlockState block) {
        return (T) block.getBlockEntity().orElse(null);
    }

    default <T> Optional<T> getBlockStateEntity(BlockState block) {
        return (Optional<T>) block.getBlockEntity();
    }
}