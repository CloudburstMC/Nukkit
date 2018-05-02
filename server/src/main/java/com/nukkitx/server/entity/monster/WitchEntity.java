package com.nukkitx.server.entity.monster;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.monster.Witch;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.LivingEntity;
import com.nukkitx.server.level.NukkitLevel;

public class WitchEntity extends LivingEntity implements Witch {

    public WitchEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.WITCH, position, level, server, 26);
    }
}
