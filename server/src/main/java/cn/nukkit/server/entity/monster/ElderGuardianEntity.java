package cn.nukkit.server.entity.monster;

import cn.nukkit.api.entity.monster.ElderGuardian;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.LivingEntity;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public class ElderGuardianEntity extends LivingEntity implements ElderGuardian {

    public ElderGuardianEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.ELDER_GUARDIAN, position, level, server, 80);
    }
}
