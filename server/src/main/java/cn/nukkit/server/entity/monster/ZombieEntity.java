package cn.nukkit.server.entity.monster;

import cn.nukkit.api.entity.monster.Zombie;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.LivingEntity;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public class ZombieEntity extends LivingEntity implements Zombie {

    public ZombieEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.ZOMBIE, position, level, server, 20);
    }
}
