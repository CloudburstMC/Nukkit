package com.nukkitx.server.entity.projectile;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.component.Physics;
import com.nukkitx.api.entity.projectile.Arrow;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.BaseEntity;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.component.PhysicsComponent;
import com.nukkitx.server.level.NukkitLevel;

public class ArrowEntity extends BaseEntity implements Arrow {

    public ArrowEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.ARROW, position, level, server);

        this.registerComponent(Physics.class, new PhysicsComponent(0.05f, 0.01f));
    }

    public void setCritical() {
        this.setCritical(true);
    }

    public void setCritical(boolean value) {
        this.setFlag(MetadataConstants.Flag.CRITICAL, value);
    }

    public boolean isCritical() {
        return this.getFlag(MetadataConstants.Flag.CRITICAL);
    }

    /*@Override
    public int getResultDamage() {
        int base = super.getResultDamage();

        if (this.isCritical()) {
            base += this.level.rand.nextInt(base / 2 + 2);
        }

        return base;
    }

    @Override
    protected double getBaseDamage() {
        return 2;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        this.timing.startTiming();

        boolean hasUpdate = super.onUpdate(currentTick);

        if (this.onGround || this.hadCollision) {
            this.setCritical(false);
        }

        if (this.age > 1200) {
            this.close();
            hasUpdate = true;
        }

        this.timing.stopTiming();

        return hasUpdate;
    }*/

}
