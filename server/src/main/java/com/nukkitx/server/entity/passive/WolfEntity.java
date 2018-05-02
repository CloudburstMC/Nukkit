package com.nukkitx.server.entity.passive;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.passive.Wolf;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.LivingEntity;
import com.nukkitx.server.level.NukkitLevel;

public class WolfEntity extends LivingEntity implements Wolf {

    public static final int NETWORK_ID = 14;

    public WolfEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.WOLF, position, level, server, 8);
    }
}
