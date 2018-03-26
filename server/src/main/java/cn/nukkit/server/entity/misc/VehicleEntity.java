package cn.nukkit.server.entity.misc;

import cn.nukkit.api.entity.component.Damageable;
import cn.nukkit.api.entity.component.Physics;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.BaseEntity;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.component.DamageableComponent;
import cn.nukkit.server.entity.component.PhysicsComponent;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public abstract class VehicleEntity extends BaseEntity {

    public VehicleEntity(EntityType type, Vector3f position, NukkitLevel level, NukkitServer server, int maximumHealth) {
        super(type, position, level, server);

        registerComponent(Physics.class, new PhysicsComponent(0.1f, 0.1f));
        registerComponent(Damageable.class, new DamageableComponent(this, maximumHealth));
    }
}
