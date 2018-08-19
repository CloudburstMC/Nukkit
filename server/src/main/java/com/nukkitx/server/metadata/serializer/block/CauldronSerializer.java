package com.nukkitx.server.metadata.serializer.block;

import com.flowpowered.math.GenericMath;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.Cauldron;
import com.nukkitx.server.metadata.serializer.MetadataSerializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class CauldronSerializer implements MetadataSerializer<Cauldron> {

    public static final MetadataSerializer INSTANCE = new CauldronSerializer();

    @Override
    public short readMetadata(Cauldron data) {
        return data.getLevel();
    }

    @Override
    public Cauldron writeMetadata(ItemType type, short metadata) {
        return Cauldron.of(GenericMath.clamp(metadata, 0, 4));
    }
}
