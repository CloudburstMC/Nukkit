package com.nukkitx.server.entity.monster;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.monster.Wither;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.LivingEntity;
import com.nukkitx.server.level.NukkitLevel;

public class WitherEntity extends LivingEntity implements Wither {

    public WitherEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.WITHER, position, level, server, 300);
    }
}
