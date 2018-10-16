package com.nukkitx.server.metadata.serializer.block;

import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.Trapdoor;
import com.nukkitx.api.metadata.data.SimpleDirection;
import com.nukkitx.server.math.DirectionHelper;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TrapdoorSerializer implements Serializer<Trapdoor> {

    public static final Serializer INSTANCE = new TrapdoorSerializer();

    @Override
    public short readMetadata(Trapdoor metadata) {
        short meta = DirectionHelper.toMeta(metadata.getDirection(), DirectionHelper.SeqType.TYPE_5);

        if (metadata.isOpen()) {
            meta |= 0x04;
        }

        if (metadata.isTopHalf()) {
            meta |= 0x08;
        }

        return meta;
    }

    @Override
    public Trapdoor writeMetadata(ItemType type, short metadata) {
        SimpleDirection direction = DirectionHelper.fromMeta(metadata & 0x03, DirectionHelper.SeqType.TYPE_5);

        return Trapdoor.of(direction, (metadata & 0x04) > 0, (metadata & 0x08) > 0);
    }
}
