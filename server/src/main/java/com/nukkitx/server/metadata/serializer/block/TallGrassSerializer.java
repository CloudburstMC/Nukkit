package com.nukkitx.server.metadata.serializer.block;

import com.google.common.base.Preconditions;
import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.Metadata;
import com.nukkitx.api.metadata.Metadatable;
import com.nukkitx.api.metadata.block.TallGrass;
import com.nukkitx.nbt.tag.CompoundTag;
import com.nukkitx.server.metadata.serializer.Serializer;

public class TallGrassSerializer implements Serializer {
    private static final TallGrass[] VALUES = TallGrass.values();
    @Override
    public CompoundTag readNBT(BlockState state) {
        return null;
    }

    @Override
    public short readMetadata(Metadatable metadatable) {
        return (short) metadatable.ensureMetadata(TallGrass.class).ordinal();
    }

    @Override
    public CompoundTag readNBT(ItemInstance item) {
        return null;
    }

    @Override
    public Metadata writeMetadata(ItemType type, short metadata) {
        Preconditions.checkArgument(metadata >= 0 && metadata < VALUES.length);
        return VALUES[metadata];
    }
}
