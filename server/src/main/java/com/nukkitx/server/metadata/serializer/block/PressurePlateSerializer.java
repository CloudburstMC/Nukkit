package com.nukkitx.server.metadata.serializer.block;

import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.PressurePlate;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PressurePlateSerializer implements Serializer<PressurePlate> {

    public static final Serializer INSTANCE = new PressurePlateSerializer();

    @Override
    public short readMetadata(PressurePlate metadata) {
        return (short) (metadata.isActive() ? 1 : 0);
    }

    @Override
    public PressurePlate writeMetadata(ItemType type, short metadata) {
        return metadata == 0 ? PressurePlate.INACTIVE : PressurePlate.ACTIVE;
    }
}
