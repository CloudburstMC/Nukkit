package com.nukkitx.server.entity.monster;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.monster.Slime;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.LivingEntity;
import com.nukkitx.server.level.NukkitLevel;

public class SlimeEntity extends LivingEntity implements Slime {

    public SlimeEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.SLIME, position, level, server, 4);
    }
}
