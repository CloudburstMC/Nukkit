package com.nukkitx.server.metadata.serializer.block;

import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.Cactus;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * @author CreeperFace
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CactusSerializer implements Serializer<Cactus> {

    public static final Serializer INSTANCE = new CactusSerializer();

    @Override
    public short readMetadata(Cactus metadata) {
        return metadata.getAge();
    }

    @Override
    public Cactus writeMetadata(ItemType type, short metadata) {
        return Cactus.of(metadata);
    }
}
