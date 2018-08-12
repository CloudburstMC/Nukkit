package com.nukkitx.server.metadata.serializer.block;

import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.Metadata;
import com.nukkitx.api.metadata.block.Bed;
import com.nukkitx.api.metadata.data.SimpleDirection;
import com.nukkitx.server.math.DirectionHelper;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BedSerializer implements Serializer {

    public static final Serializer INSTANCE = new BedSerializer();

    @Override
    public short readMetadata(BlockState block) {
        Bed data = block.ensureMetadata(Bed.class);

        short meta = DirectionHelper.toMeta(data.getDirection(), DirectionHelper.SeqType.TYPE_2);

        if (data.isOccupied()) {
            meta |= 0x04;
        }

        if (data.getPart().ordinal() == 1) {
            meta |= 0x08;
        }

        return meta;
    }

    @Override
    public Metadata writeMetadata(ItemType type, short metadata) {
        SimpleDirection direction = DirectionHelper.fromMeta(metadata & 0x03, DirectionHelper.SeqType.TYPE_2);

        return Bed.of(direction, (metadata & 0x04) == 0x04, Bed.Part.values()[(metadata & 0x08) >> 3]);
    }
}
