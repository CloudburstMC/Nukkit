package com.nukkitx.server.metadata.serializer.block;

import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.BrewingStand;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BrewingStandSerializer implements Serializer<BrewingStand> {

    public static final Serializer INSTANCE = new BrewingStandSerializer();

    @Override
    public short readMetadata(BrewingStand data) {
        short meta = 0;

        if (data.isSlotFilled(BrewingStand.SlotType.EAST)) {
            meta |= 0x01;
        }

        if (data.isSlotFilled(BrewingStand.SlotType.SOUTHWEST)) {
            meta |= 0x02;
        }

        if (data.isSlotFilled(BrewingStand.SlotType.NORTHWEST)) {
            meta |= 0x04;
        }

        return meta;
    }

    @Override
    public BrewingStand writeMetadata(ItemType type, short metadata) {
        return BrewingStand.of((metadata & 0x01) == 1, (metadata & 0x02) == 0x02, (metadata & 0x03) == 0x03);
    }
}
