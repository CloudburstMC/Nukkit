package com.nukkitx.server.metadata.serializer.block;

import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.Door;
import com.nukkitx.server.math.DirectionHelper;
import com.nukkitx.server.metadata.serializer.MetadataSerializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class DoorSerializer implements MetadataSerializer<Door> {

    public static final MetadataSerializer INSTANCE = new DoorSerializer();

    @Override
    public short readMetadata(Door data) {
        short meta = 0;

        if (data.isUpperPart()) {
            if (data.isRightHinge())
                meta |= 0x01;

            if (data.isPowered())
                meta |= 0x02;

            meta |= 0x08;
        } else {
            meta |= DirectionHelper.toMeta(data.getDirection(), DirectionHelper.SeqType.TYPE_3);

            if (data.isOpen())
                meta |= 0x04;
        }

        return meta;
    }

    @Override
    public Door writeMetadata(ItemType type, short metadata) {
        if ((metadata & 0x08) == 0x08) { //upper part
            return Door.of((metadata & 0x01) == 0x01, (metadata & 0x02) == 0x02);
        } else {
            return Door.of(DirectionHelper.fromMeta(metadata, DirectionHelper.SeqType.TYPE_3), (metadata & 0x04) == 0x04);
        }
    }
}
