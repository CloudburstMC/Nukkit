package com.nukkitx.server.metadata.serializer.block;

import com.flowpowered.math.GenericMath;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.Cocoa;
import com.nukkitx.api.metadata.data.SimpleDirection;
import com.nukkitx.server.math.DirectionHelper;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class CocoaSerializer implements Serializer<Cocoa> {

    public static final Serializer INSTANCE = new CocoaSerializer();

    @Override
    public short readMetadata(Cocoa data) {
        short meta = DirectionHelper.toMeta(data.getDirection(), DirectionHelper.SeqType.TYPE_6);

        meta |= (data.getStage().ordinal() << 2);

        return meta;
    }

    @Override
    public Cocoa writeMetadata(ItemType type, short metadata) {
        SimpleDirection direction = DirectionHelper.fromMeta(metadata & 0x03, DirectionHelper.SeqType.TYPE_6);

        Cocoa.Stage stage = Cocoa.Stage.values()[GenericMath.clamp((metadata & 0xc) >> 2, 0, Cocoa.Stage.values().length)];

        return Cocoa.of(direction, stage);
    }
}
