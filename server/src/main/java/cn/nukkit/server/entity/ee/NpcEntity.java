package cn.nukkit.server.entity.ee;

import cn.nukkit.api.entity.ee.Npc;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.LivingEntity;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public class NpcEntity extends LivingEntity implements Npc {

    protected NpcEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.NPC, position, level, server, 20);
    }
}
