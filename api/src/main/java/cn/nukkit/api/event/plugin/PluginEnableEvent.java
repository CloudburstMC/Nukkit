package cn.nukkit.api.event.plugin;

import cn.nukkit.api.plugin.Plugin;

public class PluginEnableEvent implements PluginEvent {
    private final Plugin plugin;

    public PluginEnableEvent(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }
}
