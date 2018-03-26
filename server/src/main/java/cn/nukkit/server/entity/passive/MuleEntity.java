package cn.nukkit.server.entity.passive;

import cn.nukkit.api.entity.passive.Mule;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.LivingEntity;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public class MuleEntity extends LivingEntity implements Mule {

    public MuleEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.MULE, position, level, server, 15);
    }
}
