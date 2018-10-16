package com.nukkitx.server.metadata.serializer.block;

import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.FenceGate;
import com.nukkitx.server.math.DirectionHelper;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FenceGateSerializer implements Serializer<FenceGate> {

    public static final Serializer INSTANCE = new FenceGateSerializer();

    @Override
    public short readMetadata(FenceGate metadata) {
        short meta = DirectionHelper.toMeta(metadata.getDirection(), DirectionHelper.SeqType.TYPE_2);

        if (metadata.isOpen()) {
            meta |= 0x04;
        }

        return meta;
    }

    @Override
    public FenceGate writeMetadata(ItemType type, short metadata) {
        return FenceGate.of(DirectionHelper.fromMeta(metadata & 0x03, DirectionHelper.SeqType.TYPE_2), (metadata & 0x04) > 0);
    }
}
