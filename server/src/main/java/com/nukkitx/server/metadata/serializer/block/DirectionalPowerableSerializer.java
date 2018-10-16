package com.nukkitx.server.metadata.serializer.block;

import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.DirectionalPowerable;
import com.nukkitx.server.math.DirectionHelper;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * @author CreeperFace
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DirectionalPowerableSerializer implements Serializer<DirectionalPowerable> {

    public static final Serializer INSTANCE_1 = new DirectionalPowerableSerializer(DirectionHelper.SeqType.TYPE_1);
    public static final Serializer INSTANCE_2 = new DirectionalPowerableSerializer(DirectionHelper.SeqType.TYPE_2);
    public static final Serializer INSTANCE_3 = new DirectionalPowerableSerializer(DirectionHelper.SeqType.TYPE_3);

    private final DirectionHelper.SeqType seqType;

    @Override
    public short readMetadata(DirectionalPowerable metadata) {
        short meta = DirectionHelper.toMeta(metadata.getFace(), seqType);

        if (metadata.isPowered()) {
            meta |= 0x08;
        }

        return meta;
    }

    @Override
    public DirectionalPowerable writeMetadata(ItemType type, short metadata) {
        return DirectionalPowerable.of(DirectionHelper.faceFromMeta(metadata & 0x07, seqType), (metadata & 0x08) > 0);
    }
}
