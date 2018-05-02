package com.nukkitx.server.entity.monster;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.monster.Guardian;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.LivingEntity;
import com.nukkitx.server.level.NukkitLevel;

public class GuardianEntity extends LivingEntity implements Guardian {

    public GuardianEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.GUARDIAN, position, level, server, 30);
    }
}
