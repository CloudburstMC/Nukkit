package com.nukkitx.server.entity.monster;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.monster.CaveSpider;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.LivingEntity;
import com.nukkitx.server.level.NukkitLevel;

public class CaveSpiderEntity extends LivingEntity implements CaveSpider {

    public CaveSpiderEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.CAVE_SPIDER, position, level, server, 12);
    }
}
