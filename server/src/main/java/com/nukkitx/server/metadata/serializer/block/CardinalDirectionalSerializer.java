package com.nukkitx.server.metadata.serializer.block;

import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.CardinalDirectional;
import com.nukkitx.api.metadata.data.CardinalDirection;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CardinalDirectionalSerializer implements Serializer<CardinalDirectional> {

    public static final Serializer INSTANCE = new CardinalDirectionalSerializer();

    private static final CardinalDirection[] VALUES = CardinalDirection.values();

    @Override
    public short readMetadata(CardinalDirectional metadata) {
        return (short) metadata.getDirection().ordinal();
    }

    @Override
    public CardinalDirectional writeMetadata(ItemType type, short metadata) {
        return CardinalDirectional.of(VALUES[metadata]);
    }
}
