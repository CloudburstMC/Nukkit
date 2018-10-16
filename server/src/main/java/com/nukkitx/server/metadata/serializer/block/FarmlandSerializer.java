package com.nukkitx.server.metadata.serializer.block;

import com.flowpowered.math.GenericMath;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.Farmland;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FarmlandSerializer implements Serializer<Farmland> {

    public static Serializer INSTANCE = new FarmlandSerializer();

    @Override
    public short readMetadata(Farmland metadata) {
        return metadata.getWetness();
    }

    @Override
    public Farmland writeMetadata(ItemType type, short metadata) {
        return Farmland.of(GenericMath.clamp(metadata, 0, 7));
    }
}
