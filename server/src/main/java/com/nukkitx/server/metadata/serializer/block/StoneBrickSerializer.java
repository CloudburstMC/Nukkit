package com.nukkitx.server.metadata.serializer.block;

import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.StoneBrick;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StoneBrickSerializer implements Serializer<StoneBrick> {

    public static final Serializer INSTANCE = new StoneBrickSerializer();

    private static final StoneBrick[] VALUES = StoneBrick.values();

    @Override
    public short readMetadata(StoneBrick metadata) {
        return (short) metadata.ordinal();
    }

    @Override
    public StoneBrick writeMetadata(ItemType type, short metadata) {
        return VALUES[Math.min(VALUES.length - 1, metadata)];
    }
}
