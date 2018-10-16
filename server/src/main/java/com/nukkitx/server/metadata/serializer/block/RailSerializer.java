package com.nukkitx.server.metadata.serializer.block;

import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.Rail;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RailSerializer implements Serializer<Rail> {

    public static final Serializer INSTANCE = new RailSerializer();

    private static final Rail[] VALUES = Rail.values();

    @Override
    public short readMetadata(Rail metadata) {
        return (short) metadata.ordinal();
    }

    @Override
    public Rail writeMetadata(ItemType type, short metadata) {
        return VALUES[Math.min(metadata, VALUES.length - 1)];
    }
}
