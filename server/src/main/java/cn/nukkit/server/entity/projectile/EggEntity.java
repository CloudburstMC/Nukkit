package cn.nukkit.server.entity.projectile;

import cn.nukkit.api.entity.component.Physics;
import cn.nukkit.api.entity.projectile.Egg;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.BaseEntity;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.component.PhysicsComponent;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public class EggEntity extends BaseEntity implements Egg {

    public EggEntity(Vector3f position, NukkitServer server, NukkitLevel level) {
        super(EntityType.EGG, position, level, server);

        registerComponent(Physics.class, new PhysicsComponent(0.03f, 0.01f));
    }

    /*@Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        boolean hasUpdate = super.onUpdate(currentTick);

        if (this.age > 1200 || this.isCollided) {
            this.kill();
            hasUpdate = true;
        }

        return hasUpdate;
    }*/
}
