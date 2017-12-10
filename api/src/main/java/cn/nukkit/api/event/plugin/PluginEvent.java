package cn.nukkit.api.event.plugin;

import cn.nukkit.api.event.Event;
import cn.nukkit.api.plugin.Plugin;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PluginEvent implements Event {
    private final Plugin plugin;

    public PluginEvent(Plugin plugin) {
        this.plugin = plugin;
    }

    public Plugin getPlugin() {
        return plugin;
    }
}
