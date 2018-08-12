package com.nukkitx.server.metadata.serializer.block;

import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.Metadata;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BrewingStandSerializer implements Serializer {

    public static final Serializer INSTANCE = new BrewingStandSerializer();

    @Override
    public short readMetadata(BlockState block) {
        return 0;
    }

    @Override
    public Metadata writeMetadata(ItemType type, short metadata) {
        return null;
    }
}
