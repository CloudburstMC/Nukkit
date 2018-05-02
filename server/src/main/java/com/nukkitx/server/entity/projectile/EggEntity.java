package com.nukkitx.server.entity.projectile;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.component.Physics;
import com.nukkitx.api.entity.projectile.Egg;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.BaseEntity;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.component.PhysicsComponent;
import com.nukkitx.server.level.NukkitLevel;

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
