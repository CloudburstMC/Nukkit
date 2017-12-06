package cn.nukkit.api.event.entity;

import cn.nukkit.server.entity.Entity;

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
