package com.nukkitx.server.metadata.serializer;

import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.Metadata;
import com.nukkitx.api.metadata.blockentity.BlockEntity;
import com.nukkitx.nbt.tag.CompoundTag;

@SuppressWarnings("unchecked")
public interface Serializer {
    default CompoundTag readNBT(BlockState state) {
        return null;
    }

    default short readMetadata(BlockState state) {
        return 0;
    }

    default CompoundTag readNBT(ItemInstance item) {
        return null;
    }

    default short readMetadata(ItemInstance item) {
        return 0;
    }

    default Metadata writeMetadata(ItemType type, short metadata) {
        return null;
    }

    default BlockEntity writeNBT(ItemType type, CompoundTag nbtTag) {
        return null;
    }

    default <T extends Metadata> T getBlockData(BlockState state) {
        return (T) state.getBlockData().get();
    }

    default <T extends BlockEntity> T getBlockEntity(BlockState state) {
        return (T) state.getBlockEntity().get();
    }

    default <T extends Metadata> T getItemData(ItemInstance item) {
        return (T) item.getItemData().get();
    }
}