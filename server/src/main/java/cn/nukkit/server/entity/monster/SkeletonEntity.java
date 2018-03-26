package cn.nukkit.server.entity.monster;

import cn.nukkit.api.entity.monster.Skeleton;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.LivingEntity;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public class SkeletonEntity extends LivingEntity implements Skeleton {

    public SkeletonEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.SKELETON, position, level, server, 20);
    }
}
