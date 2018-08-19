package com.nukkitx.server.metadata.serializer.block;

import com.flowpowered.math.GenericMath;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.SimpleDirectional;
import com.nukkitx.api.metadata.data.SimpleDirection;
import com.nukkitx.server.metadata.serializer.MetadataSerializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SimpleDirectionalSerializer implements MetadataSerializer<SimpleDirectional> {

    public static final MetadataSerializer INSTANCE = new SimpleDirectionalSerializer();

    @Override
    public short readMetadata(SimpleDirectional data) {
        return (short) data.getDirection().toBlockFace().ordinal();
    }

    @Override
    public SimpleDirectional writeMetadata(ItemType type, short metadata) {
        metadata = (short) GenericMath.clamp(metadata - 2, 0, SimpleDirection.values().length - 1);

        return SimpleDirectional.of(SimpleDirection.fromIndex(metadata));
    }
}
