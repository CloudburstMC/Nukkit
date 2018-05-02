package com.nukkitx.server.metadata.serializer;

import com.google.common.base.Preconditions;
import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.Metadata;
import com.nukkitx.api.metadata.block.TallGrass;
import com.nukkitx.nbt.tag.CompoundTag;

public class TallGrassSerializer implements Serializer {
    private static final TallGrass[] VALUES = TallGrass.values();
    @Override
    public CompoundTag readNBT(BlockState state) {
        return null;
    }

    @Override
    public short readMetadata(BlockState state) {
        return (short) state.ensureBlockData(TallGrass.class).ordinal();
    }

    @Override
    public CompoundTag readNBT(ItemInstance item) {
        return null;
    }

    @Override
    public short readMetadata(ItemInstance item) {
        return (short) item.ensureItemData(TallGrass.class).ordinal();
    }

    @Override
    public Metadata writeMetadata(ItemType type, short metadata) {
        Preconditions.checkArgument(metadata >= 0 && metadata < VALUES.length);
        return VALUES[metadata];
    }
}
