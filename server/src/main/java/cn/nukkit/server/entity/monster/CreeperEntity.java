package cn.nukkit.server.entity.monster;

import cn.nukkit.api.entity.monster.Creeper;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.LivingEntity;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public class CreeperEntity extends LivingEntity implements Creeper {

    public CreeperEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.CREEPER, position, level, server, 20);
    }
}
