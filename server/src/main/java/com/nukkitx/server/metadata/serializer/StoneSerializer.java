package com.nukkitx.server.metadata.serializer;

import com.google.common.base.Preconditions;
import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.Metadata;
import com.nukkitx.api.metadata.block.Stone;
import com.nukkitx.nbt.tag.CompoundTag;

public class StoneSerializer implements Serializer {
    private static final Stone[] VALUES = Stone.values();

    @Override
    public CompoundTag readNBT(BlockState block) {
        return null;
    }

    @Override
    public short readMetadata(BlockState block) {
        Stone stone = getBlockData(block);
        return (short) stone.ordinal();
    }

    @Override
    public CompoundTag readNBT(ItemInstance item) {
        return null;
    }

    @Override
    public short readMetadata(ItemInstance item) {
        Stone stone = getItemData(item);
        return (short) stone.ordinal();
    }

    @Override
    public Metadata writeMetadata(ItemType type, short metadata) {
        Preconditions.checkArgument(metadata >= 0 && metadata < VALUES.length);
        return VALUES[metadata];
    }
}
