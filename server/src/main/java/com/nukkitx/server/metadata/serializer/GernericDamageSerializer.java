package com.nukkitx.server.metadata.serializer;

import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.item.GenericDamageValue;

public class GernericDamageSerializer implements MetadataSerializer<GenericDamageValue> {

    @Override
    public short readMetadata(GenericDamageValue data) {
        return data.getDamage();
    }

    @Override
    public GenericDamageValue writeMetadata(ItemType type, short metadata) {
        return new GenericDamageValue(metadata);
    }
}
