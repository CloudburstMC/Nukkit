package com.nukkitx.server.entity.monster;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.monster.Creeper;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.LivingEntity;
import com.nukkitx.server.level.NukkitLevel;

public class CreeperEntity extends LivingEntity implements Creeper {

    public CreeperEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.CREEPER, position, level, server, 20);
    }
}
