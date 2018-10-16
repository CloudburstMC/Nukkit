package com.nukkitx.server.metadata.serializer.block;

import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.TripwireHook;
import com.nukkitx.api.metadata.data.SimpleDirection;
import com.nukkitx.server.math.DirectionHelper;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TripwireHookSerializer implements Serializer<TripwireHook> {

    public static final Serializer INSTANCE = new TripwireHookSerializer();

    @Override
    public short readMetadata(TripwireHook metadata) {
        short meta = DirectionHelper.toMeta(metadata.getDirection(), DirectionHelper.SeqType.TYPE_2);

        switch (metadata.getPosition()) {
            case MIDDLE:
                meta |= 0x04;
                break;
            case DOWN:
                meta |= 0x08;
                break;
        }

        return meta;
    }

    @Override
    public TripwireHook writeMetadata(ItemType type, short metadata) {
        SimpleDirection direction = DirectionHelper.fromMeta(metadata & 0x03, DirectionHelper.SeqType.TYPE_2);
        TripwireHook.HookPosition position = TripwireHook.HookPosition.UP;

        if ((metadata & 0x04) > 0) {
            position = TripwireHook.HookPosition.MIDDLE;
        } else if ((metadata & 0x08) > 0) {
            position = TripwireHook.HookPosition.DOWN;
        }

        return TripwireHook.of(direction, position);
    }
}
