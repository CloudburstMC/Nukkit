package com.nukkitx.server.entity.ee;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.ee.Agent;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.LivingEntity;
import com.nukkitx.server.level.NukkitLevel;

public class AgentEntity extends LivingEntity implements Agent {

    public AgentEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.AGENT, position, level, server, 20);
    }
}
