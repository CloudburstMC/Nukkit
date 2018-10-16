package com.nukkitx.server.metadata.serializer.block;

import com.flowpowered.math.GenericMath;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.Flower;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FlowerSerializer implements Serializer<Flower> {

    public static Serializer INSTANCE = new FlowerSerializer();

    @Override
    public short readMetadata(Flower metadata) {
        return (short) metadata.ordinal();
    }

    @Override
    public Flower writeMetadata(ItemType type, short metadata) {
        return Flower.values()[GenericMath.clamp(metadata, 0, 8)];
    }
}
