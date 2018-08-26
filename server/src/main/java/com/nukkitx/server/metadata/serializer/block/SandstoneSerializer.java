package com.nukkitx.server.metadata.serializer.block;

import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.Sandstone;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SandstoneSerializer implements Serializer<Sandstone> {

    private static final Sandstone[] VALUES = Sandstone.values();

    public static final Serializer INSTANCE = new SandstoneSerializer();

    @Override
    public short readMetadata(Sandstone metadata) {
        return (short) metadata.ordinal();
    }

    @Override
    public Sandstone writeMetadata(ItemType type, short metadata) {
        return VALUES[Math.min(2, metadata)];
    }
}
