package com.nukkitx.server.entity.monster;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.monster.Skeleton;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.LivingEntity;
import com.nukkitx.server.level.NukkitLevel;

public class SkeletonEntity extends LivingEntity implements Skeleton {

    public SkeletonEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.SKELETON, position, level, server, 20);
    }
}
