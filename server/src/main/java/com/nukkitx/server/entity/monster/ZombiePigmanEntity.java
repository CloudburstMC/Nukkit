package com.nukkitx.server.entity.monster;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.monster.ZombiePigman;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.LivingEntity;
import com.nukkitx.server.level.NukkitLevel;

public class ZombiePigmanEntity extends LivingEntity implements ZombiePigman {

    public ZombiePigmanEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.ZOMBIE_PIGMAN, position, level, server, 20);
    }
}
