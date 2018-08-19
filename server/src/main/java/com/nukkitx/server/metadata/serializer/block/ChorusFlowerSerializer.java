package com.nukkitx.server.metadata.serializer.block;

import com.flowpowered.math.GenericMath;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.ChorusFlower;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class ChorusFlowerSerializer implements Serializer<ChorusFlower> {

    public static final Serializer INSTANCE = new ChorusFlowerSerializer();

    @Override
    public short readMetadata(ChorusFlower data) {
        return (short) data.getAge();
    }

    @Override
    public ChorusFlower writeMetadata(ItemType type, short metadata) {
        return ChorusFlower.of(GenericMath.clamp(metadata, 0, 5));
    }
}
