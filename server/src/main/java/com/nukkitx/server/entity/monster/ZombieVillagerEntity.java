package com.nukkitx.server.entity.monster;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.monster.ZombieVillager;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.LivingEntity;
import com.nukkitx.server.level.NukkitLevel;

public class ZombieVillagerEntity extends LivingEntity implements ZombieVillager {

    public ZombieVillagerEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.ZOMBIE_VILLAGER, position, level, server, 20);
    }
}
