package com.nukkitx.server.metadata.serializer.item;

import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.Metadata;
import com.nukkitx.api.metadata.item.GoldenApple;
import com.nukkitx.server.metadata.serializer.Serializer;

import static com.nukkitx.api.metadata.item.GoldenApple.ENCHANTED;
import static com.nukkitx.api.metadata.item.GoldenApple.REGULAR;

public class GoldenAppleSerializer implements Serializer {
    @Override
    public short readMetadata(ItemInstance item) {
        GoldenApple goldenApple = item.ensureMetadata(GoldenApple.class);
        return (short) (goldenApple.isEnchanted() ? 1 : 0);
    }

    @Override
    public Metadata writeMetadata(ItemType type, short metadata) {
        return metadata != 0 ? ENCHANTED : REGULAR;
    }
}
