package com.nukkitx.server.metadata.serializer.block;

import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.DaylightSensor;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DaylightSensorSerializer implements Serializer<DaylightSensor> {

    public static final Serializer INSTANCE = new DaylightSensorSerializer();

    @Override
    public short readMetadata(DaylightSensor metadata) {
        return metadata.getLevel();
    }

    @Override
    public DaylightSensor writeMetadata(ItemType type, short metadata) {
        return DaylightSensor.of((byte) metadata);
    }
}
