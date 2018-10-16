package com.nukkitx.server.metadata.serializer.block;

import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.ActivableRail;
import com.nukkitx.api.metadata.data.ActivableRailDirection;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ActivableRailSerializer implements Serializer<ActivableRail> {

    public static final Serializer INSTANCE = new ActivableRailSerializer();

    private static final ActivableRailDirection[] VALUES = ActivableRailDirection.values();

    @Override
    public short readMetadata(ActivableRail metadata) {
        short meta = (short) metadata.getDirection().ordinal();

        if (metadata.isActive()) {
            meta |= 0x08;
        }

        return meta;
    }

    @Override
    public ActivableRail writeMetadata(ItemType type, short metadata) {
        return ActivableRail.of(VALUES[Math.min(metadata & 0x07, VALUES.length - 1)], (metadata & 0x08) > 0);
    }
}
