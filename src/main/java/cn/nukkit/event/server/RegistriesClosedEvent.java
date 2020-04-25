package cn.nukkit.event.server;

import cn.nukkit.event.HandlerList;
import cn.nukkit.pack.PackManager;

/**
 * Fired immediately after the registries are closed.
 *
 * @author DaPorkchop_
 */
public class RegistriesClosedEvent extends ServerEvent {
    private static final HandlerList handlers = new HandlerList();
    private final PackManager packManager;

    public RegistriesClosedEvent(PackManager packManager) {
        this.packManager = packManager;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public PackManager getPackManager() {
        return this.packManager;
    }
}
