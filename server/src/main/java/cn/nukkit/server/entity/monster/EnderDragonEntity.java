package cn.nukkit.server.entity.monster;

import cn.nukkit.api.entity.monster.EnderDragon;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.LivingEntity;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public class EnderDragonEntity extends LivingEntity implements EnderDragon {

    public EnderDragonEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.ENDER_DRAGON, position, level, server, 100);
    }
}
