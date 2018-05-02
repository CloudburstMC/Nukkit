package com.nukkitx.server.entity.passive;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.passive.Squid;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.LivingEntity;
import com.nukkitx.server.level.NukkitLevel;

public class SquidEntity extends LivingEntity implements Squid {

    public SquidEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.SQUID, position, level, server, 10);
    }
/*
    @Override
    public Item[] getDrops() {
        return new Item[]{new ItemDye(DyeColor.BLACK.getDyeData())};
    }*/
}
