package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.world.World;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EntityLevelChangeEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final World originLevel;
    private final World targetLevel;

    public EntityLevelChangeEvent(Entity entity, World originLevel, World targetLevel) {
        this.entity = entity;
        this.originLevel = originLevel;
        this.targetLevel = targetLevel;
    }

    public World getOrigin() {
        return originLevel;
    }

    public World getTarget() {
        return targetLevel;
    }
}
