package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.Event;

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
