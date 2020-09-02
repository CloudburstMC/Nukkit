package cn.nukkit.event.server;

import cn.nukkit.event.HandlerList;

/**
 * @author NycuRO (NukkitX Project)
 */
public class ServerStopEvent extends ServerEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }
}
