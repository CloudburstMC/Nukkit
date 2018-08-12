package com.nukkitx.server.metadata.serializer.block;

import com.flowpowered.math.GenericMath;
import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.Metadata;
import com.nukkitx.api.metadata.block.Cake;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CakeSerializer implements Serializer {

    public static final Serializer INSTANCE = new CakeSerializer();

    @Override
    public short readMetadata(BlockState state) {
        return state.ensureMetadata(Cake.class).getLevel();
    }

    @Override
    public Metadata writeMetadata(ItemType type, short metadata) {
        metadata = (short) GenericMath.clamp(metadata, 0, 7);

        return Cake.of(metadata);
    }
}
