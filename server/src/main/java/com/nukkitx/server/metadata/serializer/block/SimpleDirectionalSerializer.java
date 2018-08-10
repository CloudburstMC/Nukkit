package com.nukkitx.server.metadata.serializer.block;

import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.Metadata;
import com.nukkitx.api.metadata.Metadatable;
import com.nukkitx.api.metadata.block.SimpleDirectional;
import com.nukkitx.api.metadata.data.SimpleDirection;
import com.nukkitx.server.metadata.serializer.Serializer;

/**
 * @author CreeperFace
 */
public class SimpleDirectionalSerializer implements Serializer {

    @Override
    public short readMetadata(Metadatable metadatable) {
        SimpleDirectional meta = metadatable.ensureMetadata(SimpleDirectional.class);

        return (short) meta.getDirection().ordinal();
    }

    @Override
    public Metadata writeMetadata(ItemType type, short metadata) {
        return SimpleDirectional.of(SimpleDirection.fromIndex(metadata));
    }
}
