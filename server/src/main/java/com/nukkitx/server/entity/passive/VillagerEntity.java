package com.nukkitx.server.entity.passive;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.component.Ageable;
import com.nukkitx.api.entity.component.Professionable;
import com.nukkitx.api.entity.passive.Villager;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.LivingEntity;
import com.nukkitx.server.entity.component.AgeableComponent;
import com.nukkitx.server.entity.component.ProfessionableComponent;
import com.nukkitx.server.level.NukkitLevel;

public class VillagerEntity extends LivingEntity implements Villager {

    public VillagerEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.VILLAGER, position, level, server, 20);

        registerComponent(Ageable.class, new AgeableComponent(24000));
        registerComponent(Professionable.class, new ProfessionableComponent());
    }
}
