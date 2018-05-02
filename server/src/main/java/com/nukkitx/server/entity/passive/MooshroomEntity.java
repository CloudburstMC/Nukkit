package com.nukkitx.server.entity.passive;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.passive.Mooshroom;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.LivingEntity;
import com.nukkitx.server.level.NukkitLevel;

public class MooshroomEntity extends LivingEntity implements Mooshroom {

    public MooshroomEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.MOOSHROOM, position, level, server, 10);
    }
/*
    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.LEATHER), Item.get(Item.RAW_BEEF)};
    }*/
}