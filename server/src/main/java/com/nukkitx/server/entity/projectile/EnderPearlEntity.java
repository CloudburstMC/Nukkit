package com.nukkitx.server.entity.projectile;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.projectile.EnderPearl;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.BaseEntity;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.level.NukkitLevel;

public class EnderPearlEntity extends BaseEntity implements EnderPearl {

    public EnderPearlEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.ENDER_PEARL, position, level, server);
    }

    /*@Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        this.timing.startTiming();

        boolean hasUpdate = super.onUpdate(currentTick);

        if (this.isCollided && this.shootingEntity instanceof PlayerOld) {
            this.shootingEntity.teleport(new Vector3(NukkitMath.floorDouble(this.x) + 0.5, this.y, NukkitMath.floorDouble(this.z) + 0.5), TeleportCause.ENDER_PEARL);
            if ((((PlayerOld) this.shootingEntity).getGamemode() & 0x01) == 0) this.shootingEntity.attack(5);
            this.level.addSound(new EndermanTeleportSound(this));
        }

        if (this.age > 1200 || this.isCollided) {
            this.kill();
            hasUpdate = true;
        }

        this.timing.stopTiming();

        return hasUpdate;
    }*/
}
