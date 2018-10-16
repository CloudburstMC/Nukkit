package com.nukkitx.server.metadata.serializer.block;

import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.NetherPortal;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PortalSerializer implements Serializer<NetherPortal> {

    public static final Serializer INSTANCE = new PortalSerializer();

    private final static NetherPortal[] VALUES = NetherPortal.values();

    @Override
    public short readMetadata(NetherPortal metadata) {
        return (short) metadata.ordinal();
    }

    @Override
    public NetherPortal writeMetadata(ItemType type, short metadata) {
        return VALUES[Math.min(VALUES.length - 1, metadata)];
    }
}
