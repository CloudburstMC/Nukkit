package com.nukkitx.server.entity.passive;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.component.Ageable;
import com.nukkitx.api.entity.passive.Chicken;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.LivingEntity;
import com.nukkitx.server.entity.component.AgeableComponent;
import com.nukkitx.server.level.NukkitLevel;

public class ChickenEntity extends LivingEntity implements Chicken {

    public ChickenEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.CHICKEN, position, level, server, 4);

        registerComponent(Ageable.class, new AgeableComponent(24000));
    }
/*
    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.RAW_CHICKEN), Item.get(Item.FEATHER)};
    }

    @Override
    public boolean isBreedingItem(Item item) {
        int id = item.getId();

        return id == Item.WHEAT_SEEDS || id == Item.MELON_SEEDS || id == Item.PUMPKIN_SEEDS || id == Item.BEETROOT_SEEDS;
    }*/
}
