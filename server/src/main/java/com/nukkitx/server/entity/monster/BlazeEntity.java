package com.nukkitx.server.entity.monster;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.LivingEntity;
import com.nukkitx.server.level.NukkitLevel;

public class BlazeEntity extends LivingEntity {

    public BlazeEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.BLAZE, position, level, server, 20);
    }
}
