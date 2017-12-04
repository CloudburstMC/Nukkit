package cn.nukkit.server.event.entity;

import cn.nukkit.server.entity.Entity;
import cn.nukkit.server.event.Event;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class EntityEvent extends Event {

    protected Entity entity;

    public Entity getEntity() {
        return entity;
    }
}
