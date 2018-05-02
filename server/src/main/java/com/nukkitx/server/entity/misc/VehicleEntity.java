package com.nukkitx.server.entity.misc;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.component.Damageable;
import com.nukkitx.api.entity.component.Physics;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.BaseEntity;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.component.DamageableComponent;
import com.nukkitx.server.entity.component.PhysicsComponent;
import com.nukkitx.server.level.NukkitLevel;

public abstract class VehicleEntity extends BaseEntity {

    public VehicleEntity(EntityType type, Vector3f position, NukkitLevel level, NukkitServer server, int maximumHealth) {
        super(type, position, level, server);

        registerComponent(Physics.class, new PhysicsComponent(0.1f, 0.1f));
        registerComponent(Damageable.class, new DamageableComponent(this, maximumHealth));
    }
}
