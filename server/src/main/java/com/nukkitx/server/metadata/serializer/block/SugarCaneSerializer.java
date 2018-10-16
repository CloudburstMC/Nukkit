package com.nukkitx.server.metadata.serializer.block;

import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.SugarCane;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SugarCaneSerializer implements Serializer<SugarCane> {

    public static final Serializer INSTANCE = new SugarCaneSerializer();

    @Override
    public short readMetadata(SugarCane metadata) {
        return metadata.getAge();
    }

    @Override
    public SugarCane writeMetadata(ItemType type, short metadata) {
        return SugarCane.of(metadata);
    }
}
