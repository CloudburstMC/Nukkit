package com.nukkitx.server.metadata.serializer.block;

import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.SimpleDirectional;
import com.nukkitx.server.math.DirectionHelper;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * @author CreeperFace
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SimpleDirectionalSerializer implements Serializer<SimpleDirectional> {

    public static final Serializer INSTANCE = new SimpleDirectionalSerializer(DirectionHelper.SeqType.TYPE_1, 2); //furnace, chest etc
    public static final Serializer INSTANCE_1 = new SimpleDirectionalSerializer(DirectionHelper.SeqType.TYPE_1, 0);
    public static final Serializer INSTANCE_2 = new SimpleDirectionalSerializer(DirectionHelper.SeqType.TYPE_2, 0);
    public static final Serializer INSTANCE_3 = new SimpleDirectionalSerializer(DirectionHelper.SeqType.TYPE_3, 0);
    public static final Serializer INSTANCE_4 = new SimpleDirectionalSerializer(DirectionHelper.SeqType.TYPE_4, 0);
    public static final Serializer INSTANCE_2_REVERSED = new SimpleDirectionalSerializer(DirectionHelper.SeqType.TYPE_6, 0);

    private final DirectionHelper.SeqType type;
    private final int offset;

    @Override
    public short readMetadata(SimpleDirectional data) {
        return (short) (DirectionHelper.toMeta(data.getDirection(), this.type) + offset);
    }

    @Override
    public SimpleDirectional writeMetadata(ItemType type, short metadata) {
        return SimpleDirectional.of(DirectionHelper.fromMeta(metadata - offset, this.type));
    }
}
