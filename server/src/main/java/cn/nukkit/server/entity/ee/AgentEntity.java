package cn.nukkit.server.entity.ee;

import cn.nukkit.api.entity.ee.Agent;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.LivingEntity;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public class AgentEntity extends LivingEntity implements Agent {

    public AgentEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.AGENT, position, level, server, 20);
    }
}
