package com.nukkitx.server.entity.monster;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.monster.Endermite;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.LivingEntity;
import com.nukkitx.server.level.NukkitLevel;

public class EndermiteEntity extends LivingEntity implements Endermite {

    public EndermiteEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.ENDERMITE, position, level, server, 8);
    }
}
