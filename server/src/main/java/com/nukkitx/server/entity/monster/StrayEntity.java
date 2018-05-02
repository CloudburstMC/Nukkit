package com.nukkitx.server.entity.monster;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.monster.Stray;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.LivingEntity;
import com.nukkitx.server.level.NukkitLevel;

public class StrayEntity extends LivingEntity implements Stray {

    public StrayEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.STRAY, position, level, server, 20);
    }
}
