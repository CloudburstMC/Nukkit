package com.nukkitx.server.metadata.serializer.block;

import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.Dyed;
import com.nukkitx.api.metadata.data.DyeColor;
import com.nukkitx.server.metadata.serializer.MetadataSerializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DyedSerializer implements MetadataSerializer<Dyed> {

    public static final MetadataSerializer INSTANCE = new DyedSerializer();

    @Override
    public short readMetadata(Dyed data) {
        return (short) data.getColor().ordinal();
    }

    @Override
    public Dyed writeMetadata(ItemType type, short metadata) {
        return Dyed.of(DyeColor.values()[metadata]);
    }
}
