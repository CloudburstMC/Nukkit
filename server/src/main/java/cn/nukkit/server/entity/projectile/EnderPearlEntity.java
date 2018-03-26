package cn.nukkit.server.entity.projectile;

import cn.nukkit.api.entity.projectile.EnderPearl;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.BaseEntity;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

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
