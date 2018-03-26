package cn.nukkit.server.entity.monster;

import cn.nukkit.api.entity.monster.Wither;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.LivingEntity;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public class WitherEntity extends LivingEntity implements Wither {

    public WitherEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.WITHER, position, level, server, 300);
    }
}
