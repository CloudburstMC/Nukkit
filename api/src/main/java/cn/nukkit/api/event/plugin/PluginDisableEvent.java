package cn.nukkit.api.event.plugin;

import cn.nukkit.api.plugin.Plugin;

public class PluginDisableEvent implements PluginEvent {
    private final Plugin plugin;

    public PluginDisableEvent(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }
}
