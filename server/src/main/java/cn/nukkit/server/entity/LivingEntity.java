package cn.nukkit.server.entity;

import cn.nukkit.api.entity.component.Armorable;
import cn.nukkit.api.entity.component.Damageable;
import cn.nukkit.api.entity.component.Physics;
import cn.nukkit.api.level.Level;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.component.ArmorableComponent;
import cn.nukkit.server.entity.component.DamageableComponent;
import com.flowpowered.math.vector.Vector3f;

public class LivingEntity extends BaseEntity {
    protected LivingEntity(EntityTypeData data, Level level, Vector3f position, NukkitServer server, int maximumHealth) {
        super(data, position, level, server);

        this.registerComponent(Damageable.class, new DamageableComponent(maximumHealth));
        this.registerComponent(Armorable.class, new ArmorableComponent());
        this.registerComponent(Physics.class, new PhysicsComponent());
    }
}
