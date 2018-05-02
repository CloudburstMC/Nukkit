package com.nukkitx.server.entity.monster;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.monster.MagmaCube;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.LivingEntity;
import com.nukkitx.server.level.NukkitLevel;

public class MagmaCubeEntity extends LivingEntity implements MagmaCube {

    public MagmaCubeEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.MAGMA_CUBE, position, level, server, 16);
    }
}
