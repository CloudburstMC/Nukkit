package com.nukkitx.server.entity.misc;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.component.Physics;
import com.nukkitx.api.entity.misc.FallingBlock;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.BaseEntity;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.component.PhysicsComponent;
import com.nukkitx.server.level.NukkitLevel;

public class FallingBlockEntity extends BaseEntity implements FallingBlock {

    public FallingBlockEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.FALLING_BLOCK, position, level, server);

        registerComponent(Physics.class, new PhysicsComponent(0.04f, 0.02f));
        // ContainedBlock.class
    }
}
