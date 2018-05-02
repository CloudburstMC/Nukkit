package com.nukkitx.server.entity.monster;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.monster.EnderDragon;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.LivingEntity;
import com.nukkitx.server.level.NukkitLevel;

public class EnderDragonEntity extends LivingEntity implements EnderDragon {

    public EnderDragonEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.ENDER_DRAGON, position, level, server, 100);
    }
}
