package com.nukkitx.api.event.plugin;

import com.nukkitx.api.plugin.Plugin;

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
