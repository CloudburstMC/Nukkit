package com.nukkitx.server.metadata.serializer.block;

import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.Metadata;
import com.nukkitx.api.metadata.block.Cake;
import com.nukkitx.server.metadata.serializer.Serializer;

public class CakeSerializer implements Serializer {
    @Override
    public short readMetadata(BlockState state) {
        return state.ensureMetadata(Cake.class).getLevel();
    }

    @Override
    public Metadata writeMetadata(ItemType type, short metadata) {
        return Cake.of(metadata);
    }
}
