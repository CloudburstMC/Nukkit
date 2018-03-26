package cn.nukkit.server.entity.misc;

import cn.nukkit.api.entity.component.ContainedExperience;
import cn.nukkit.api.entity.component.Physics;
import cn.nukkit.api.entity.misc.ExperienceBottle;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.BaseEntity;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.component.ContainedExperienceComponent;
import cn.nukkit.server.entity.component.PhysicsComponent;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public class ExperienceBottleEntity extends BaseEntity implements ExperienceBottle {

    public ExperienceBottleEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.EXPERIENCE_BOTTLE, position, level, server);

        registerComponent(Physics.class, new PhysicsComponent(0.1f, 0.01f));
        registerComponent(ContainedExperience.class, new ContainedExperienceComponent(20));
    }
}
