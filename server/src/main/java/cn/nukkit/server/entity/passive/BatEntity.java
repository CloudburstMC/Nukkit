package cn.nukkit.server.entity.passive;

import cn.nukkit.api.entity.passive.Bat;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.LivingEntity;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public class BatEntity extends LivingEntity implements Bat {

    public BatEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.BAT, position, level, server, 6);
    }
}
