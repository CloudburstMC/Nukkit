package cn.nukkit.server.entity.monster;

import cn.nukkit.api.entity.monster.ZombieVillager;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.LivingEntity;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public class ZombieVillagerEntity extends LivingEntity implements ZombieVillager {

    public ZombieVillagerEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.ZOMBIE_VILLAGER, position, level, server, 20);
    }
}
