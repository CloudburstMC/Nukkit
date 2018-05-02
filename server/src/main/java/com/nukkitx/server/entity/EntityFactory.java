package com.nukkitx.server.entity;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.Entity;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.level.NukkitLevel;

@FunctionalInterface
public interface EntityFactory<T extends Entity> {
    T newInstance(Vector3f position, NukkitLevel level, NukkitServer server);
}
