package com.nukkitx.api.event.entity;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.Entity;

public class EntitySpawnEvent implements EntityEvent {
    private final Entity entity;

    public EntitySpawnEvent(Entity entity) {
        this.entity = entity;
    }

    public Vector3f getPosition() {
        return this.entity.getPosition();
    }

    @Override
    public Entity getEntity() {
        return entity;
    }
}
