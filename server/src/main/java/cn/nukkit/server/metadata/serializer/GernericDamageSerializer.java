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
import cn.nukkit.api.metadata.item.GenericDamageValue;
import cn.nukkit.server.nbt.tag.CompoundTag;

import java.util.Optional;

public class GernericDamageSerializer implements Serializer {

    @Override
    public CompoundTag readNBT(BlockState block) {
        return null;
    }

    @Override
    public short readMetadata(BlockState block) {
        return 0;
    }

    @Override
    public CompoundTag readNBT(ItemInstance item) {
        return null;
    }

    @Override
    public short readMetadata(ItemInstance item) {
        Optional<GenericDamageValue> optional = getItemData(item);
        return optional.map(GenericDamageValue::getDamage).orElse((short) 0);
    }

    @Override
    public Metadata writeMetadata(ItemType type, short metadata) {
        return metadata == 0 ? null : new GenericDamageValue(metadata);
    }

    @Override
    public BlockEntity writeNBT(ItemType type, CompoundTag nbtTag) {
        return null;
    }
}
