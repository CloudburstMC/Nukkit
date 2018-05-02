package com.nukkitx.server.entity.projectile;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.projectile.Snowball;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.BaseEntity;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.level.NukkitLevel;

public class SnowballEntity extends BaseEntity implements Snowball {

    public SnowballEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.SNOWBALL, position, level, server);
    }

    /*@Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        this.timing.startTiming();

        boolean hasUpdate = super.onUpdate(currentTick);

        if (this.age > 1200 || this.isCollided) {
            this.kill();
            hasUpdate = true;
        }

        this.timing.stopTiming();

        return hasUpdate;
    }*/
}
