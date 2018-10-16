package com.nukkitx.server.metadata.serializer.block;

import com.nukkitx.api.block.BlockTypes;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.Log;
import com.nukkitx.api.metadata.data.LogDirection;
import com.nukkitx.api.metadata.data.TreeSpecies;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LogSerializer implements Serializer<Log> {

    public static final Serializer INSTANCE = new LogSerializer();

    private static TreeSpecies[] SPIECES = TreeSpecies.values();
    private static LogDirection[] DIRECTIONS = LogDirection.values();

    @Override
    public short readMetadata(Log metadata) {
        short meta = (short) (metadata.getSpecies().ordinal() % 4);

        meta |= (metadata.getDirection().ordinal() << 2);

        return meta;
    }

    @Override
    public Log writeMetadata(ItemType type, short metadata) {
        TreeSpecies species;

        if (type == BlockTypes.WOOD) {
            species = SPIECES[metadata & 0x03];
        } else {
            species = SPIECES[(metadata & 0x03) + 2];
        }

        LogDirection direction = DIRECTIONS[metadata >> 2];

        return Log.of(species, direction);
    }
}
