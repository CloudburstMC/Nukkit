package com.nukkitx.server.entity.monster;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.monster.Silverfish;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.LivingEntity;
import com.nukkitx.server.level.NukkitLevel;

public class SilverfishEntity extends LivingEntity implements Silverfish {

    public SilverfishEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.SILVERFISH, position, level, server, 8);
    }
}
