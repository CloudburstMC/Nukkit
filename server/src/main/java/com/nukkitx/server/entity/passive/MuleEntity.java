package com.nukkitx.server.entity.passive;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.passive.Mule;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.LivingEntity;
import com.nukkitx.server.level.NukkitLevel;

public class MuleEntity extends LivingEntity implements Mule {

    public MuleEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.MULE, position, level, server, 15);
    }
}
