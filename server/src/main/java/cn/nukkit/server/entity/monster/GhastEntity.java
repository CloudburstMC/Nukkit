package cn.nukkit.server.entity.monster;

import cn.nukkit.api.entity.monster.Ghast;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.LivingEntity;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public class GhastEntity extends LivingEntity implements Ghast {

    public GhastEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.GHAST, position, level, server, 10);
    }
}
