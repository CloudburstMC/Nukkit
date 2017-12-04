package cn.nukkit.server.event.entity;

import cn.nukkit.server.entity.Entity;
import cn.nukkit.server.event.Cancellable;
import cn.nukkit.server.event.HandlerList;

public class EntityPortalEnterEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final PortalType type;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public EntityPortalEnterEvent(Entity entity, PortalType type) {
        this.entity = entity;
        this.type = type;
    }

    public PortalType getPortalType() {
        return type;
    }

    public enum PortalType {
        NETHER,
        END
    }
}
