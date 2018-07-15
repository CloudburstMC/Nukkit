package com.nukkitx.server.metadata.serializer;

import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.Metadata;
import com.nukkitx.api.metadata.item.GenericDamageValue;

public class GernericDamageSerializer implements Serializer {

    @Override
    public short readMetadata(ItemInstance item) {
        GenericDamageValue damage = item.ensureItemData(GenericDamageValue.class);
        return damage.getDamage();
    }

    @Override
    public Metadata writeMetadata(ItemType type, short metadata) {
        return new GenericDamageValue(metadata);
    }
}
