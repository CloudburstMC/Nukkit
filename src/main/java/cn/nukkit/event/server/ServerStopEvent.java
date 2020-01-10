package cn.nukkit.event.server;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

/**
 * author: NycuRO
 * NukkitX Project
 */
public class ServerStopEvent extends ServerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }
}
