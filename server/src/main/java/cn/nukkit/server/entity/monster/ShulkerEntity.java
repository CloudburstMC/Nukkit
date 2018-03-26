package cn.nukkit.server.entity.monster;

import cn.nukkit.api.entity.monster.Shulker;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.LivingEntity;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public class ShulkerEntity extends LivingEntity implements Shulker {

    public ShulkerEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.SHULKER, position, level, server, 15);
    }
}
