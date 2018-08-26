package com.nukkitx.server.metadata.serializer.block;

import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.StoneSlab2;
import com.nukkitx.api.metadata.data.StoneSlabType2;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StoneSlab2Serializer implements Serializer<StoneSlab2> {

    public static final Serializer INSTANCE = new StoneSlab2Serializer();

    private static final StoneSlabType2[] VALUES = StoneSlabType2.values();

    @Override
    public short readMetadata(StoneSlab2 metadata) {
        short meta = (short) metadata.getType().ordinal();

        if (metadata.isUpper()) {
            meta |= 0x08;
        }

        return meta;
    }

    @Override
    public StoneSlab2 writeMetadata(ItemType type, short metadata) {
        StoneSlabType2 slabType = VALUES[metadata & 0x0c];

        return StoneSlab2.of(slabType, (metadata & 0x08) > 0, false);
    }
}
