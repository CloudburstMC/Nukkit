package com.nukkitx.server.metadata.serializer;

import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.blockentity.BlockEntity;
import com.nukkitx.nbt.tag.CompoundTag;

/**
 * @author CreeperFace
 */
public interface NBTSerializer {

    default CompoundTag readNBT(BlockState state) {
        return null;
    }

    default CompoundTag readNBT(ItemInstance item) {
        return null;
    }

    default BlockEntity writeNBT(ItemType type, CompoundTag nbtTag) {
        return null;
    }
}
