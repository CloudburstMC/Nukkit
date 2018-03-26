package cn.nukkit.server.entity.monster;

import cn.nukkit.api.entity.monster.Spider;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.LivingEntity;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public class SpiderEntity extends LivingEntity implements Spider {

    public SpiderEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.SPIDER, position, level, server, 16);
    }
}
