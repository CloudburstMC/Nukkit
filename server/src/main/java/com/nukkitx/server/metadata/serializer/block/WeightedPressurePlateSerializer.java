package com.nukkitx.server.metadata.serializer.block;

import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.WeightedPressurePlate;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WeightedPressurePlateSerializer implements Serializer<WeightedPressurePlate> {

    public static final Serializer INSTANCE = new WeightedPressurePlateSerializer();

    @Override
    public short readMetadata(WeightedPressurePlate metadata) {
        return metadata.getPowerLevel();
    }

    @Override
    public WeightedPressurePlate writeMetadata(ItemType type, short metadata) {
        return WeightedPressurePlate.of(metadata);
    }
}
