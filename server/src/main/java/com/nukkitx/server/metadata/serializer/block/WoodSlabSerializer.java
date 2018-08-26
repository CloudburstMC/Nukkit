package com.nukkitx.server.metadata.serializer.block;

import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.WoodSlab;
import com.nukkitx.api.metadata.data.TreeSpecies;
import com.nukkitx.server.metadata.serializer.Serializer;

/**
 * @author CreeperFace
 */
public class WoodSlabSerializer implements Serializer<WoodSlab> {

    public static final Serializer INSTANCE = new WoodSlabSerializer();

    private static final TreeSpecies[] VALUES = TreeSpecies.values();

    @Override
    public short readMetadata(WoodSlab metadata) {
        short meta = (short) metadata.getType().ordinal();

        if (metadata.isUpper()) {
            meta |= 0x08;
        }

        return meta;
    }

    @Override
    public WoodSlab writeMetadata(ItemType type, short metadata) {
        TreeSpecies slabType = VALUES[Math.min(metadata & 0x0c, VALUES.length - 1)];

        return WoodSlab.of(slabType, (metadata & 0x08) > 0, false);
    }
}
