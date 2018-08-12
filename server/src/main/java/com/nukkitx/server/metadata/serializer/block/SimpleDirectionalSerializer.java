package com.nukkitx.server.metadata.serializer.block;

import com.flowpowered.math.GenericMath;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.Metadata;
import com.nukkitx.api.metadata.Metadatable;
import com.nukkitx.api.metadata.block.SimpleDirectional;
import com.nukkitx.api.metadata.data.SimpleDirection;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SimpleDirectionalSerializer implements Serializer {

    public static final Serializer INSTANCE = new SimpleDirectionalSerializer();

    @Override
    public short readMetadata(Metadatable metadatable) {
        SimpleDirectional meta = metadatable.ensureMetadata(SimpleDirectional.class);

        return (short) meta.getDirection().toBlockFace().ordinal();
    }

    @Override
    public Metadata writeMetadata(ItemType type, short metadata) {
        metadata = (short) GenericMath.clamp(metadata - 2, 0, SimpleDirection.values().length - 1);

        return SimpleDirectional.of(SimpleDirection.fromIndex(metadata));
    }
}
