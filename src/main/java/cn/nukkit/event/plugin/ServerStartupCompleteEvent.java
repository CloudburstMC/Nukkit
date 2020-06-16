package cn.nukkit.event.plugin;

import cn.nukkit.event.HandlerList;
import cn.nukkit.event.server.ServerEvent;

public class ServerStartupCompleteEvent extends ServerEvent {
    private static final HandlerList handlers = new HandlerList();
    public static HandlerList getHandlers() {
        return handlers;
    }

}
