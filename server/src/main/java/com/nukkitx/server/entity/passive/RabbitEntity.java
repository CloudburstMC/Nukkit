package com.nukkitx.server.entity.passive;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.passive.Rabbit;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.LivingEntity;
import com.nukkitx.server.level.NukkitLevel;

public class RabbitEntity extends LivingEntity implements Rabbit {

    public static final int NETWORK_ID = 18;

    public RabbitEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.RABBIT, position, level, server, 10);
    }
/*
    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.RAW_RABBIT), Item.get(Item.RABBIT_HIDE), Item.get(Item.RABBIT_FOOT)};
    }*/
}
