package cn.nukkit.server.entity.monster;

import cn.nukkit.api.entity.monster.Enderman;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.LivingEntity;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public class EndermanEntity extends LivingEntity implements Enderman {

    public EndermanEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.ENDERMAN, position, level, server, 40);
    }
}
