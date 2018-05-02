package com.nukkitx.server.entity.passive;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.passive.ZombieHorse;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.LivingEntity;
import com.nukkitx.server.level.NukkitLevel;

public class ZombieHorseEntity extends LivingEntity implements ZombieHorse {

    public ZombieHorseEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.ZOMBIE_HORSE, position, level, server, 15);
    }
/*
    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.ROTTEN_FLESH, 1, 1)};
    }*/
}
