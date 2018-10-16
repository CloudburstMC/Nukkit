package com.nukkitx.server.metadata.serializer.block;

import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.Stairs;
import com.nukkitx.server.math.DirectionHelper;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StairsSerializer implements Serializer<Stairs> {

    public static final Serializer INSTANCE = new StairsSerializer();

    @Override
    public short readMetadata(Stairs metadata) {
        return (short) (DirectionHelper.toMeta(metadata.getDirection(), DirectionHelper.SeqType.TYPE_4) | (metadata.isUpsideDown() ? 0x04 : 0));
    }

    @Override
    public Stairs writeMetadata(ItemType type, short metadata) {
        return Stairs.of(DirectionHelper.fromMeta(metadata & 0x03, DirectionHelper.SeqType.TYPE_4), (metadata & 0x04) > 0);
    }
}
