package cn.nukkit.server.entity.monster;

import cn.nukkit.api.entity.monster.WitherSkeleton;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.LivingEntity;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public class WitherSkeletonEntity extends LivingEntity implements WitherSkeleton {

    public WitherSkeletonEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.WITHER_SKELETON, position, level, server, 20);
    }
}
