package com.nukkitx.server.metadata.serializer.block;

import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.MonsterEgg;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MonsterEggSerializer implements Serializer<MonsterEgg> {

    public static final Serializer INSTANCE = new MonsterEggSerializer();

    private static final MonsterEgg[] VALUES = MonsterEgg.values();

    @Override
    public short readMetadata(MonsterEgg metadata) {
        return (short) metadata.ordinal();
    }

    @Override
    public MonsterEgg writeMetadata(ItemType type, short metadata) {
        return VALUES[Math.min(metadata, VALUES.length - 1)];
    }
}
