package cn.nukkit.server.entity.passive;

import cn.nukkit.api.entity.passive.Wolf;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.LivingEntity;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public class WolfEntity extends LivingEntity implements Wolf {

    public static final int NETWORK_ID = 14;

    public WolfEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.WOLF, position, level, server, 8);
    }
}
