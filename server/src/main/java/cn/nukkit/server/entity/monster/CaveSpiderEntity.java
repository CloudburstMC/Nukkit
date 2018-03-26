package cn.nukkit.server.entity.monster;

import cn.nukkit.api.entity.monster.CaveSpider;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.LivingEntity;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public class CaveSpiderEntity extends LivingEntity implements CaveSpider {

    public CaveSpiderEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.CAVE_SPIDER, position, level, server, 12);
    }
}
