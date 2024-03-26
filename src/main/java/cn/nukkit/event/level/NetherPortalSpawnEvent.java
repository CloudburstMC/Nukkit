package cn.nukkit.event.level;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Position;

public class NetherPortalSpawnEvent extends LevelEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Position portalPosition;

    public NetherPortalSpawnEvent(Position portalPosition) {
        super(portalPosition.getLevel());

        this.portalPosition = portalPosition.clone();
    }

    public Position getPortalPosition() {
        return this.portalPosition;
    }
}
