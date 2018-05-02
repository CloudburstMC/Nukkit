package com.nukkitx.server.entity.misc;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.vehicle.Boat;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.level.NukkitLevel;

public class BoatEntity extends VehicleEntity implements Boat {

    public BoatEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.BOAT, position, level, server, 5);
    }
}