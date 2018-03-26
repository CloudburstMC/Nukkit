package cn.nukkit.server.entity.projectile;

import cn.nukkit.api.entity.projectile.Snowball;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.BaseEntity;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

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
