package cn.nukkit.api.event.plugin;

import cn.nukkit.server.event.HandlerList;
import cn.nukkit.server.plugin.Plugin;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PluginEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Plugin plugin;

    public PluginEvent(Plugin plugin) {
        this.plugin = plugin;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Plugin getPlugin() {
        return plugin;
    }
}
