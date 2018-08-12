package com.nukkitx.server.metadata.serializer.block;

import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.Dyed;
import com.nukkitx.api.metadata.Metadata;
import com.nukkitx.api.metadata.Metadatable;
import com.nukkitx.api.metadata.data.DyeColor;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DyedSerializer implements Serializer {

    public static final Serializer INSTANCE = new DyedSerializer();

    @Override
    public short readMetadata(Metadatable metadatable) {
        Dyed data = metadatable.ensureMetadata(Dyed.class);

        return (short) data.getColor().ordinal();
    }

    @Override
    public Metadata writeMetadata(ItemType type, short metadata) {
        return Dyed.of(DyeColor.values()[metadata]);
    }
}
