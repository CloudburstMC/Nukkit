package com.nukkitx.server.metadata.serializer.block;

import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.RedstoneRepeater;
import com.nukkitx.server.math.DirectionHelper;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedstoneRepeaterSerializer implements Serializer<RedstoneRepeater> {

    public static final Serializer INSTANCE = new RedstoneRepeaterSerializer();

    @Override
    public short readMetadata(RedstoneRepeater metadata) {
        return (short) (DirectionHelper.toMeta(metadata.getDirection(), DirectionHelper.SeqType.TYPE_2) | (metadata.getDelay() << 2));
    }

    @Override
    public RedstoneRepeater writeMetadata(ItemType type, short metadata) {
        return RedstoneRepeater.of(DirectionHelper.fromMeta(metadata & 0x03, DirectionHelper.SeqType.TYPE_2), metadata >> 2);
    }
}
