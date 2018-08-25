package com.nukkitx.server.metadata.serializer.block;

import com.flowpowered.math.GenericMath;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.Jukebox;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JukeboxSerializer implements Serializer<Jukebox> {

    public static final Serializer INSTANCE = new JukeboxSerializer();
    private static final Jukebox[] VALUES = Jukebox.values();

    @Override
    public short readMetadata(Jukebox metadata) {
        return (short) metadata.ordinal();
    }

    @Override
    public Jukebox writeMetadata(ItemType type, short metadata) {
        return VALUES[GenericMath.clamp(metadata, 0, VALUES.length)];
    }
}
