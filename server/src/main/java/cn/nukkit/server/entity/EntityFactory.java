package cn.nukkit.server.entity;

import cn.nukkit.api.entity.Entity;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

@FunctionalInterface
public interface EntityFactory<T extends Entity> {
    T newInstance(Vector3f position, NukkitLevel level, NukkitServer server);
}
