package cn.nukkit.server.entity.monster;

import cn.nukkit.api.entity.monster.Stray;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.LivingEntity;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public class StrayEntity extends LivingEntity implements Stray {

    public StrayEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.STRAY, position, level, server, 20);
    }
}
