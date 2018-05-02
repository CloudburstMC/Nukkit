package com.nukkitx.api.event.plugin;

import com.nukkitx.api.plugin.Plugin;

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
