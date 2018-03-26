package cn.nukkit.server.entity;

import cn.nukkit.api.entity.component.Damageable;
import cn.nukkit.api.entity.component.Equippable;
import cn.nukkit.api.entity.component.Physics;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.component.DamageableComponent;
import cn.nukkit.server.entity.component.EquippableComponent;
import cn.nukkit.server.entity.component.PhysicsComponent;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public abstract class LivingEntity extends BaseEntity {

    protected LivingEntity(EntityType data, Vector3f position, NukkitLevel level, NukkitServer server, int maximumHealth) {
        super(data, position, level, server);

        this.registerComponent(Damageable.class, new DamageableComponent(this, maximumHealth));
        this.registerComponent(Equippable.class, new EquippableComponent());
        this.registerComponent(Physics.class, new PhysicsComponent(0.08f, 0.02f));
    }
}
