package com.nukkitx.server.entity.monster;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.monster.ElderGuardian;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.LivingEntity;
import com.nukkitx.server.level.NukkitLevel;

public class ElderGuardianEntity extends LivingEntity implements ElderGuardian {

    public ElderGuardianEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.ELDER_GUARDIAN, position, level, server, 80);
    }
}
