package com.nukkitx.server.entity.ee;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.ee.Camera;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.BaseEntity;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.level.NukkitLevel;

public class CameraEntity extends BaseEntity implements Camera {

    public CameraEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.TRIPOD_CAMERA, position, level, server);
    }
}
