package com.nukkitx.server.metadata.serializer.block;

import com.flowpowered.math.GenericMath;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.TallGrass;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TallGrassSerializer implements Serializer<TallGrass> {

    public static final Serializer INSTANCE = new TallGrassSerializer();

    private static final TallGrass[] VALUES = TallGrass.values();

    @Override
    public short readMetadata(TallGrass data) {
        return (short) data.ordinal();
    }

    @Override
    public TallGrass writeMetadata(ItemType type, short metadata) {
        metadata = (short) GenericMath.clamp(metadata, 0, VALUES.length - 1);

        return VALUES[metadata];
    }
}
