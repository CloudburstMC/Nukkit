package cn.nukkit.server.entity.misc;

import cn.nukkit.api.entity.vehicle.Boat;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public class BoatEntity extends VehicleEntity implements Boat {

    public BoatEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.BOAT, position, level, server, 5);
    }
}