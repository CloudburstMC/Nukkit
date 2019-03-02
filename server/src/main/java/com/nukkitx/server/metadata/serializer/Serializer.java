package com.nukkitx.server.metadata.serializer;

import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.item.ItemStack;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.Metadata;
import com.nukkitx.api.metadata.blockentity.BlockEntity;
import com.nukkitx.nbt.tag.CompoundTag;

public interface Serializer<T extends Metadata> {

    default short readMetadata(T metadata) {
        return 0;
    }

    default T writeMetadata(ItemType type, short metadata) {
        return null;
    }

    default CompoundTag readNBT(BlockState state) {
        return null;
    }

    default CompoundTag readNBT(ItemStack item) {
        return null;
    }

    default BlockEntity writeNBT(ItemType type, CompoundTag nbtTag) {
        return null;
    }
}