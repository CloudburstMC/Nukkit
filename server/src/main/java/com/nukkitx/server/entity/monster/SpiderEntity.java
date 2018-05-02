package com.nukkitx.server.entity.monster;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.monster.Spider;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.LivingEntity;
import com.nukkitx.server.level.NukkitLevel;

public class SpiderEntity extends LivingEntity implements Spider {

    public SpiderEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.SPIDER, position, level, server, 16);
    }
}
