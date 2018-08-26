package com.nukkitx.server.metadata.serializer.block;

import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.Directional;
import com.nukkitx.server.math.DirectionHelper;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * @author CreeperFace
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DirectionalSerializer implements Serializer<Directional> {

    public static final Serializer INSTANCE = new DirectionalSerializer(DirectionHelper.SeqType.TYPE_1);

    private final DirectionHelper.SeqType seqType;

    @Override
    public short readMetadata(Directional metadata) {
        return DirectionHelper.toMeta(metadata.getFace(), seqType);
    }

    @Override
    public Directional writeMetadata(ItemType type, short metadata) {
        return Directional.of(DirectionHelper.faceFromMeta(metadata, seqType));
    }
}
