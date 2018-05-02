package com.nukkitx.server.entity.passive;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.passive.PolarBear;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.LivingEntity;
import com.nukkitx.server.level.NukkitLevel;

public class PolarBearEntity extends LivingEntity implements PolarBear {

    public static final int NETWORK_ID = 28;

    public PolarBearEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.POLAR_BEAR, position, level, server, 30);
    }
/*
    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.RAW_FISH), Item.get(Item.RAW_SALMON)};
    }*/
}
