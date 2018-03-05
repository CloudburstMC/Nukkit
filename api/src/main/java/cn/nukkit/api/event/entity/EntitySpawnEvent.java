package cn.nukkit.api.event.entity;

import cn.nukkit.api.entity.Entity;
import com.flowpowered.math.vector.Vector3f;

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
