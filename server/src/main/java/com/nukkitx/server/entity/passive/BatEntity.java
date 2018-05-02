package com.nukkitx.server.entity.passive;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.passive.Bat;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.LivingEntity;
import com.nukkitx.server.level.NukkitLevel;

public class BatEntity extends LivingEntity implements Bat {

    public BatEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.BAT, position, level, server, 6);
    }
}
