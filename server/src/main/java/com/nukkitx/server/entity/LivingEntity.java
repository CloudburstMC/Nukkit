package com.nukkitx.server.entity;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.component.Damageable;
import com.nukkitx.api.entity.component.Equippable;
import com.nukkitx.api.entity.component.Physics;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.component.DamageableComponent;
import com.nukkitx.server.entity.component.EquippableComponent;
import com.nukkitx.server.entity.component.PhysicsComponent;
import com.nukkitx.server.level.NukkitLevel;

public abstract class LivingEntity extends BaseEntity {

    protected LivingEntity(EntityType data, Vector3f position, NukkitLevel level, NukkitServer server, int maximumHealth) {
        super(data, position, level, server);

        this.registerComponent(Damageable.class, new DamageableComponent(this, maximumHealth));
        this.registerComponent(Equippable.class, new EquippableComponent());
        this.registerComponent(Physics.class, new PhysicsComponent(0.08f, 0.02f));
    }
}
