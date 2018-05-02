package com.nukkitx.server.entity.monster;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.monster.Shulker;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.LivingEntity;
import com.nukkitx.server.level.NukkitLevel;

public class ShulkerEntity extends LivingEntity implements Shulker {

    public ShulkerEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.SHULKER, position, level, server, 15);
    }
}
