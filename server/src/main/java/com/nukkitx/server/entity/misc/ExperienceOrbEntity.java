package com.nukkitx.server.entity.misc;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.component.ContainedExperience;
import com.nukkitx.api.entity.component.Physics;
import com.nukkitx.api.entity.component.PickupDelay;
import com.nukkitx.api.entity.misc.ExperienceOrb;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.BaseEntity;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.component.ContainedExperienceComponent;
import com.nukkitx.server.entity.component.PhysicsComponent;
import com.nukkitx.server.entity.component.PickupDelayComponent;
import com.nukkitx.server.level.NukkitLevel;


public class ExperienceOrbEntity extends BaseEntity implements ExperienceOrb {

    public ExperienceOrbEntity(Vector3f position, NukkitLevel level, NukkitServer server, int experience) {
        super(EntityType.XP_ORB, position, level, server);

        registerComponent(ContainedExperience.class, new ContainedExperienceComponent(experience));
        registerComponent(PickupDelay.class, new PickupDelayComponent(20));
        registerComponent(Physics.class, new PhysicsComponent(0.04f, 0.02f));
    }
}