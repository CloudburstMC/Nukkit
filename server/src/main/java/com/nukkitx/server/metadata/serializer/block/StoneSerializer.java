package com.nukkitx.server.metadata.serializer.block;

import com.flowpowered.math.GenericMath;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.Stone;
import com.nukkitx.server.metadata.serializer.MetadataSerializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StoneSerializer implements MetadataSerializer<Stone> {

    public static final MetadataSerializer INSTANCE = new StoneSerializer();

    private static final Stone[] VALUES = Stone.values();

    @Override
    public short readMetadata(Stone data) {
        return (short) data.ordinal();
    }

    @Override
    public Stone writeMetadata(ItemType type, short metadata) {
        metadata = (short) GenericMath.clamp(metadata, 0, VALUES.length - 1);

        return VALUES[metadata];
    }
}
