package com.nukkitx.server.entity.passive;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.passive.Pig;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.LivingEntity;
import com.nukkitx.server.level.NukkitLevel;

public class PigEntity extends LivingEntity implements Pig {

    public PigEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.PIG, position, level, server, 10);
    }
/*
    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.RAW_PORKCHOP)};
    }

    @Override
    public boolean isBreedingItem(Item item) {
        int id = item.getId();

        return id == Item.CARROT || id == Item.POTATO || id == Item.BEETROOT;
    }*/
}
