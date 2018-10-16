package com.nukkitx.server.metadata.serializer.block;

import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.TopSnow;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TopSnowSerializer implements Serializer<TopSnow> {

    public static final Serializer INSTANCE = new TopSnowSerializer();

    @Override
    public short readMetadata(TopSnow metadata) {
        return metadata.getLayers();
    }

    @Override
    public TopSnow writeMetadata(ItemType type, short metadata) {
        return TopSnow.of(Math.min(7, metadata));
    }
}
