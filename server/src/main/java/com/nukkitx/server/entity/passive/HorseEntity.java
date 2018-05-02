package com.nukkitx.server.entity.passive;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.passive.Horse;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.LivingEntity;
import com.nukkitx.server.level.NukkitLevel;

public class HorseEntity extends LivingEntity implements Horse {

    public HorseEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.HORSE, position, level, server, 15);
    }
    /*
    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.LEATHER)};
    }*/
}
