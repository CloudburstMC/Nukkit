package com.nukkitx.server.metadata.serializer.block;

import com.flowpowered.math.GenericMath;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.Cake;
import com.nukkitx.server.metadata.serializer.MetadataSerializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CakeSerializer implements MetadataSerializer<Cake> {

    public static final MetadataSerializer INSTANCE = new CakeSerializer();

    @Override
    public short readMetadata(Cake data) {
        return data.getLevel();
    }

    @Override
    public Cake writeMetadata(ItemType type, short metadata) {
        metadata = (short) GenericMath.clamp(metadata, 0, 7);

        return Cake.of(metadata);
    }
}
