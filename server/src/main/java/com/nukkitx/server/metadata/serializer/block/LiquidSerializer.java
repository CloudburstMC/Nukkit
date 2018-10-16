package com.nukkitx.server.metadata.serializer.block;

import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.Liquid;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LiquidSerializer implements Serializer<Liquid> {

    public static final LiquidSerializer INSTANCE = new LiquidSerializer();

    @Override
    public short readMetadata(Liquid metadata) {
        short meta = metadata.getLevel();

        if (metadata.isFalling()) {
            meta |= 0x08;
        }

        return meta;
    }

    @Override
    public Liquid writeMetadata(ItemType type, short metadata) {
        boolean falling = (metadata & 0x08) > 0;

        return Liquid.of(metadata & 0x07, falling);
    }
}
