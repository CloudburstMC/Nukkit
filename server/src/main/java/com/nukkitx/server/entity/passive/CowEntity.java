package com.nukkitx.server.entity.passive;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.passive.Cow;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.LivingEntity;
import com.nukkitx.server.level.NukkitLevel;

public class CowEntity extends LivingEntity implements Cow {

    public CowEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.COW, position, level, server, 10);
    }
/*
    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.LEATHER), Item.get(Item.RAW_BEEF)};
    }*/
}
