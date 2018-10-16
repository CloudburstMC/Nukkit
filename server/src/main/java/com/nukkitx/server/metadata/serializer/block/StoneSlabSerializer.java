package com.nukkitx.server.metadata.serializer.block;

import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.StoneSlab;
import com.nukkitx.api.metadata.data.StoneSlabType;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StoneSlabSerializer implements Serializer<StoneSlab> {

    public static final Serializer INSTANCE = new StoneSlabSerializer();

    private static final StoneSlabType[] VALUES = StoneSlabType.values();

    @Override
    public short readMetadata(StoneSlab metadata) {
        short meta = (short) metadata.getType().ordinal();

        if (metadata.isUpper()) {
            meta |= 0x08;
        }

        return meta;
    }

    @Override
    public StoneSlab writeMetadata(ItemType type, short metadata) {
        StoneSlabType slabType = VALUES[Math.min(metadata & 0x0c, VALUES.length - 1)];

        return StoneSlab.of(slabType, (metadata & 0x08) > 0, false);
    }
}
