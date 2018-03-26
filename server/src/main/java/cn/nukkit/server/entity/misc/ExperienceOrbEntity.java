package cn.nukkit.server.entity.misc;

import cn.nukkit.api.entity.component.ContainedExperience;
import cn.nukkit.api.entity.component.Physics;
import cn.nukkit.api.entity.component.PickupDelay;
import cn.nukkit.api.entity.misc.ExperienceOrb;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.BaseEntity;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.component.ContainedExperienceComponent;
import cn.nukkit.server.entity.component.PhysicsComponent;
import cn.nukkit.server.entity.component.PickupDelayComponent;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;


public class ExperienceOrbEntity extends BaseEntity implements ExperienceOrb {

    public ExperienceOrbEntity(Vector3f position, NukkitLevel level, NukkitServer server, int experience) {
        super(EntityType.EXPERIENCE_ORB, position, level, server);

        registerComponent(ContainedExperience.class, new ContainedExperienceComponent(experience));
        registerComponent(PickupDelay.class, new PickupDelayComponent(20));
        registerComponent(Physics.class, new PhysicsComponent(0.04f, 0.02f));
    }
}