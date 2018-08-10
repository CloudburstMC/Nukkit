package com.nukkitx.server.metadata.serializer;

import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.Metadata;
import com.nukkitx.api.metadata.Metadatable;
import com.nukkitx.api.metadata.blockentity.BlockEntity;
import com.nukkitx.nbt.tag.CompoundTag;

@SuppressWarnings("unchecked")
public interface Serializer {
    default CompoundTag readNBT(BlockState state) {
        return null;
    }

    default CompoundTag readNBT(ItemInstance item) {
        return null;
    }

    default short readMetadata(ItemInstance item) {
        return readMetadata((Metadatable) item);
    }

    default short readMetadata(BlockState block) {
        return readMetadata((Metadatable) block);
    }

    default short readMetadata(Metadatable metadatable) {
        return 0;
    }

    default Metadata writeMetadata(ItemType type, short metadata) {
        return null;
    }

    default BlockEntity writeNBT(ItemType type, CompoundTag nbtTag) {
        return null;
    }
}