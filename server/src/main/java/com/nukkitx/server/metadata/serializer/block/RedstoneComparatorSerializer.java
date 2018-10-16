package com.nukkitx.server.metadata.serializer.block;

import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.RedstoneComparator;
import com.nukkitx.server.math.DirectionHelper;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedstoneComparatorSerializer implements Serializer<RedstoneComparator> {

    public static final Serializer INSTANCE = new RedstoneComparatorSerializer();

    @Override
    public short readMetadata(RedstoneComparator metadata) {
        short meta = DirectionHelper.toMeta(metadata.getDirection(), DirectionHelper.SeqType.TYPE_6);

        if (metadata.isSubtractionMode()) {
            meta |= 0x04;
        }

        if (metadata.isPowered()) {
            meta |= 0x08;
        }

        return meta;
    }

    @Override
    public RedstoneComparator writeMetadata(ItemType type, short metadata) {
        return RedstoneComparator.of(DirectionHelper.fromMeta(metadata & 0x03, DirectionHelper.SeqType.TYPE_6), (metadata & 0x04) > 0, (metadata & 0x08) > 0);
    }
}
