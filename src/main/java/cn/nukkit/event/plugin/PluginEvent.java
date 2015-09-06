package cn.nukkit.event.plugin;

import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.plugin.Plugin;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PluginEvent<T extends Plugin> extends Event {

    private static final HandlerList handlers = new HandlerList();

    private T plugin;

    public PluginEvent(T plugin) {
        this.plugin = plugin;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public T getPlugin() {
        return plugin;
    }
}
