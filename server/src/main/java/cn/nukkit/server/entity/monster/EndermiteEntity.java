package cn.nukkit.server.entity.monster;

import cn.nukkit.api.entity.monster.Endermite;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.LivingEntity;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public class EndermiteEntity extends LivingEntity implements Endermite {

    public EndermiteEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.ENDERMITE, position, level, server, 8);
    }
}
