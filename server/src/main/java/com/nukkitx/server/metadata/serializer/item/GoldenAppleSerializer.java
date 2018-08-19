package com.nukkitx.server.metadata.serializer.item;

import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.item.GoldenApple;
import com.nukkitx.server.metadata.serializer.MetadataSerializer;

import static com.nukkitx.api.metadata.item.GoldenApple.ENCHANTED;
import static com.nukkitx.api.metadata.item.GoldenApple.REGULAR;

public class GoldenAppleSerializer implements MetadataSerializer<GoldenApple> {
    @Override
    public short readMetadata(GoldenApple data) {
        return (short) (data.isEnchanted() ? 1 : 0);
    }

    @Override
    public GoldenApple writeMetadata(ItemType type, short metadata) {
        return metadata != 0 ? ENCHANTED : REGULAR;
    }
}
