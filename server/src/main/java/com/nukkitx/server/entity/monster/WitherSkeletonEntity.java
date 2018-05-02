package com.nukkitx.server.entity.monster;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.monster.WitherSkeleton;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.LivingEntity;
import com.nukkitx.server.level.NukkitLevel;

public class WitherSkeletonEntity extends LivingEntity implements WitherSkeleton {

    public WitherSkeletonEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.WITHER_SKELETON, position, level, server, 20);
    }
}
