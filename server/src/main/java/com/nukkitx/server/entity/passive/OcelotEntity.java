package com.nukkitx.server.entity.passive;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.passive.Ocelot;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.LivingEntity;
import com.nukkitx.server.level.NukkitLevel;

public class OcelotEntity extends LivingEntity implements Ocelot {

    public OcelotEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.OCELOT, position, level, server, 8);
    }
/*
    @Override
    public boolean isBreedingItem(Item item) {
        return item.getId() == Item.RAW_FISH;
    }*/
}
