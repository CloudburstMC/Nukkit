package com.nukkitx.server.entity.monster;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.monster.Enderman;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.LivingEntity;
import com.nukkitx.server.level.NukkitLevel;

public class EndermanEntity extends LivingEntity implements Enderman {

    public EndermanEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.ENDERMAN, position, level, server, 40);
    }
}
