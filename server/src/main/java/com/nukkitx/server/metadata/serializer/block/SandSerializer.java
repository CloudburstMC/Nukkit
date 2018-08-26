package com.nukkitx.server.metadata.serializer.block;

import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.Sand;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SandSerializer implements Serializer<Sand> {

    public static final Serializer INSTANCE = new SandSerializer();

    @Override
    public short readMetadata(Sand metadata) {
        return (short) (metadata.isRed() ? 1 : 0);
    }

    @Override
    public Sand writeMetadata(ItemType type, short metadata) {
        return Sand.of(metadata != 0);
    }
}
