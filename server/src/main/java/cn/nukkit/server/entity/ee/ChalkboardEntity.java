package cn.nukkit.server.entity.ee;

import cn.nukkit.api.entity.ee.Chalkboard;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.BaseEntity;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public class ChalkboardEntity extends BaseEntity implements Chalkboard {

    public ChalkboardEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.CHALKBOARD, position, level, server);
    }
}
