package cn.nukkit.server.entity.monster;

import cn.nukkit.api.entity.monster.Witch;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.LivingEntity;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public class WitchEntity extends LivingEntity implements Witch {

    public WitchEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.WITCH, position, level, server, 26);
    }
}
