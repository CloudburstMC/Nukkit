package cn.nukkit.server.entity.ee;

import cn.nukkit.api.entity.ee.Camera;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.BaseEntity;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public class CameraEntity extends BaseEntity implements Camera {

    public CameraEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.TRIPOD_CAMERA, position, level, server);
    }
}
