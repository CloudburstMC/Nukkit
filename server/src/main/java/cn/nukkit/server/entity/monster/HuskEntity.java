package cn.nukkit.server.entity.monster;

import cn.nukkit.api.entity.monster.Husk;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.LivingEntity;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public class HuskEntity extends LivingEntity implements Husk {

    public HuskEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.HUSK, position, level, server, 20);
    }
}
