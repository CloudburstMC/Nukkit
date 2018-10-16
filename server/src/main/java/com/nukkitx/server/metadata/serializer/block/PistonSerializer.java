package com.nukkitx.server.metadata.serializer.block;

import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.Piston;
import com.nukkitx.api.util.data.BlockFace;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PistonSerializer implements Serializer<Piston> {

    public static final Serializer INSTANCE = new PistonSerializer();

    @Override
    public short readMetadata(Piston metadata) {
        short meta;

        if (metadata.isSixSided()) {
            meta = 0x06;
        } else {
            meta = (short) metadata.getFace().ordinal();
        }

        if (metadata.isExtended()) {
            meta |= 0x08;
        }

        return meta;
    }

    @Override
    public Piston writeMetadata(ItemType type, short metadata) {
        boolean extended = (metadata & 0x08) > 0;

        if ((metadata & 0x06) > 0 || (metadata & 0x07) > 0) {
            return Piston.of(extended);
        }

        BlockFace face = BlockFace.values()[metadata & 0x07];

        return Piston.of(face, extended);
    }
}
