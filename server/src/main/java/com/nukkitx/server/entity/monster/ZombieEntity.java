package com.nukkitx.server.entity.monster;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.monster.Zombie;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.LivingEntity;
import com.nukkitx.server.level.NukkitLevel;

public class ZombieEntity extends LivingEntity implements Zombie {

    public ZombieEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.ZOMBIE, position, level, server, 20);
    }
}
