package com.nukkitx.server.metadata.serializer.block;

import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.Lever;
import com.nukkitx.api.metadata.data.SimpleDirection;
import com.nukkitx.server.math.DirectionHelper;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LeverSerializer implements Serializer<Lever> {

    public static final Serializer INSTANCE = new LeverSerializer();

    @Override
    public short readMetadata(Lever metadata) {
        short meta = 0;

        SimpleDirection dir = metadata.getDirection();
        Lever.LeverPos pos = metadata.getPosition();

        if (pos == Lever.LeverPos.TOP) {
            if (dir == SimpleDirection.SOUTH || dir == SimpleDirection.NORTH) {
                meta = 5;
            } else {
                meta = 6;
            }
        } else if (pos == Lever.LeverPos.BOTTOM) {
            if (dir == SimpleDirection.SOUTH || dir == SimpleDirection.NORTH) {
                meta = 7;
            }
        } else {
            meta += DirectionHelper.toMeta(dir, DirectionHelper.SeqType.TYPE_4) + 1;
        }

        if (metadata.isPowered()) {
            meta |= 0x08;
        }

        return meta;
    }

    @Override
    public Lever writeMetadata(ItemType type, short metadata) {
        Lever.LeverPos pos;
        SimpleDirection dir;
        boolean powered = (metadata & 0x08) == 0x08;

        if (metadata == 0 || metadata == 7) {
            pos = Lever.LeverPos.BOTTOM;

            if (metadata == 0)
                dir = SimpleDirection.EAST;
            else
                dir = SimpleDirection.SOUTH;
        } else if (metadata == 5 || metadata == 6) {
            pos = Lever.LeverPos.TOP;

            if (metadata == 6)
                dir = SimpleDirection.EAST;
            else
                dir = SimpleDirection.SOUTH;
        } else {
            pos = Lever.LeverPos.SIDE;
            dir = DirectionHelper.fromMeta((metadata & 0x07) - 1, DirectionHelper.SeqType.TYPE_4);
        }

        return Lever.of(dir, pos, powered);
    }
}
