package com.nukkitx.server.entity.misc;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.component.ContainedExperience;
import com.nukkitx.api.entity.component.Physics;
import com.nukkitx.api.entity.misc.ExperienceBottle;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.BaseEntity;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.component.ContainedExperienceComponent;
import com.nukkitx.server.entity.component.PhysicsComponent;
import com.nukkitx.server.level.NukkitLevel;

public class ExperienceBottleEntity extends BaseEntity implements ExperienceBottle {

    public ExperienceBottleEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.XP_BOTTLE, position, level, server);

        registerComponent(Physics.class, new PhysicsComponent(0.1f, 0.01f));
        registerComponent(ContainedExperience.class, new ContainedExperienceComponent(20));
    }
}
