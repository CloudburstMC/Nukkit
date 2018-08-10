package com.nukkitx.server.metadata.serializer.block;

import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.Metadata;
import com.nukkitx.api.metadata.Metadatable;
import com.nukkitx.api.metadata.block.SimpleDirectional;
import com.nukkitx.api.metadata.data.SimpleDirection;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.inject.Singleton;

/**
 * @author CreeperFace
 */
@Singleton
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SimpleDirectionalSerializer implements Serializer {

    public static final SimpleDirectionalSerializer INSTANCE = new SimpleDirectionalSerializer();

    @Override
    public short readMetadata(Metadatable metadatable) {
        SimpleDirectional meta = metadatable.ensureMetadata(SimpleDirectional.class);

        return (short) meta.getDirection().toBlockFace().ordinal();
    }

    @Override
    public Metadata writeMetadata(ItemType type, short metadata) {
        return SimpleDirectional.of(SimpleDirection.fromIndex(metadata - 2));
    }
}
