package com.nukkitx.server.metadata.serializer;

import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.Metadata;

public interface MetadataSerializer<T extends Metadata> {

    default short readMetadata(T metadata) {
        return 0;
    }

    default T writeMetadata(ItemType type, short metadata) {
        return null;
    }
}