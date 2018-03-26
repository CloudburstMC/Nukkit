package cn.nukkit.server.entity.monster;

import cn.nukkit.api.entity.monster.Silverfish;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.LivingEntity;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public class SilverfishEntity extends LivingEntity implements Silverfish {

    public SilverfishEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.SILVERFISH, position, level, server, 8);
    }
}
